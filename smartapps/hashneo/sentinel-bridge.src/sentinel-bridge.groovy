/**
 *  sentinel-bridge
 *
 *  Copyright 2020 Steven Taylor
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.codec.binary.Base64

definition(
    name: "Sentinel-Bridge",
    namespace: "hashneo",
    author: "Steven Taylor",
    description: "Sentinel Bridge",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    singleInstance: true
)

preferences {
	page(name: "page1")
}

def page1() {
  dynamicPage(name: "page1", install: true, uninstall: true) {
    section("SmartThings Hub") {
      input "hostHub", "hub", title: "Select Hub", multiple: false, required: true
    }
    section("Sentinel") {
      input "emailAddress", "text", title: "Email Address", description: "", required: true
      input "password", "password", title: "Password", description: "", required: true
    }
  }
}

def installed() {
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	initialize()
}

def initialize() {
    subscribeToEvents()
    setWebhook()
    loadDevices()
}

def subscribeToEvents() {
	subscribe(location, null, lanResponseHandler, [filterEvents:false])
}

def lanResponseHandler(evt) {
  def map = stringToMap(evt.stringValue)

  def headers = getHttpHeaders(map.headers)
  def body = getJsonFromBase64(map.body)

  if ( headers['x-security-key'] != state.securityKey ){
      return;
  }

  log.trace "Body: ${body}"

  processEvent(body)
}

private processEvent(evt) {

	def deviceId = evt.payload?.id

    if ( deviceId ){

        def childDevice = getChildDevice(deviceId)

		if ( childDevice ) {
	    	log.debug "processEvent: ${evt.type} for device id => ${deviceId}"

            switch (evt.type){
                case 'device.update':
                	childDevice.updateStatus( evt.payload?.value );
                break
            }
        }
   	}
}

private getEmail(){
	return settings.emailAddress
}

private getPassword(){
    return settings.password
}

private getToken(){
	return "Bearer ${state.token}";
}

private login(){

    try {

        if ( state?.token_exp != null ) {

            long timeDiff
            def now = new Date()
            def end =  Date.parse("yyy-MM-dd'T'HH:mm:ssZ","${state.token_exp}".replace("+00:00","+0000"))

            long unxNow = now.getTime()
            long unxEnd = end.getTime()

            unxNow = unxNow/1000
            unxEnd = unxEnd/1000

            timeDiff = Math.abs(unxNow-unxEnd)
            timeDiff = Math.round(timeDiff/60)

            log.debug "Expiration of token in ${timeDiff} minutes"

            // No need to reauth if < 6 hours
            if ( timeDiff > (6*60) )
            	return
        }

        def params = [
          uri: "https://home.steventaylor.me/api/auth/login",
          body: [
            email: getEmail(),
            password: getPassword()
          ]
        ]

        httpPostJson(params) { resp ->

    		log.trace "response: ${resp}"

            resp.headers.each {
               log.debug "${it.name} : ${it.value}"
            }

            state.token = resp.data.data.token

            String jwtToken = state.token
            String[] split_string = jwtToken.split("\\.")
            String base64EncodedHeader = split_string[0]
            String base64EncodedBody = split_string[1]
            String base64EncodedSignature = split_string[2]

            Base64 base64Url = new Base64(true)

			def obj = getJsonFromBase64(base64EncodedBody)

            state.token_exp = new Date( (obj.exp as long) * 1000 ).format("yyy-MM-dd'T'HH:mm:ssZ")

            log.debug "token => ${state.token}"
            log.debug "token expires => " + state.token_exp
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
}

private setWebhook(){

    login()

    try {
    	state.securityKey = UUID.randomUUID().toString()

        def params = [
          uri: "https://home.steventaylor.me/api/webhook/register",
          headers: [ Authorization : getToken() ],
          body: [
            url : getNotifyAddress(),
            method : "NOTIFY",
            securityKey : state.securityKey
          ]
        ]

        httpPostJson(params) { resp ->

    		log.trace "response: ${resp}"

            resp.headers.each {
               log.debug "${it.name} : ${it.value}"
            }

        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
}

private loadDevices(){

    login()

    try {
        def params = [
          uri: "https://home.steventaylor.me/api/devices",
          headers: [ Authorization : getToken() ]
        ]

        httpGet(params) { resp ->
            resp.headers.each {
                log.debug "${it.name} : ${it.value}"
            }
            log.debug "response contentType: ${resp.contentType}"
            //log.debug "response data: ${resp.data}"

            createChildDevices(resp.data)
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
}

private createChildDevices(data){

	def hostHub = location.hubs[0]

    //removeChildDevices();

    data.devices.each {
        def device = it;

        log.debug "${device.id} -> ${device.name} : ${device.type}"

        def deviceId = device.id

        if (!getChildDevice(device.id)) {

            switch ( device.type ){
                case "sensor.motion":
                      addChildDevice("hashneo", "sensor-motion", deviceId, hostHub.id, [name: device.name, label: device.name, completedSetup: true])
                      log.debug "Created new device -> ${device.name}"
                break;
                case "sensor.contact":
                      addChildDevice("hashneo", "sensor-contact", deviceId, hostHub.id, [name: device.name, label: device.name, completedSetup: true])
                      log.debug "Created new device -> ${device.name}"
                break;
            }

        }
    }
}

private getHttpHeaders(headers) {
  def obj = [:]

  def data = headers.decodeBase64();

  if (data){
      new String(data).split("\r\n").each { param ->
          def nameAndValue = param.split(":")
          obj[nameAndValue[0]] = (nameAndValue.length == 1) ? "" : nameAndValue[1].trim()
      }
  }
  return obj
}

private getJsonFromBase64(base64Data) {
  if (base64Data) {
  	return getJsonFromText( new String(base64Data.decodeBase64()) )
  }
  return null;
}

private getJsonFromText(text) {
  def obj = null
  if (text) {
    def slurper = new JsonSlurper()
    obj = slurper.parseText(text)
  }
  return obj
}

private getNotifyAddress() {
	def hostHub = location.hubs[0]
  	return "http://" + hostHub.localIP + ":" + hostHub.localSrvPortTCP + "/notify"
}

private String convertIPtoHex(ipAddress) {
  return ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join().toUpperCase()
}

private String convertPortToHex(port) {
  return port.toString().format( '%04x', port.toInteger() ).toUpperCase()
}

private removeChildDevices() {
  getAllChildDevices().each { deleteChildDevice(it.deviceNetworkId) }
}

