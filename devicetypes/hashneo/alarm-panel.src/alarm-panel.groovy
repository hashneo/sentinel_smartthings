/**
 *  alarm-panel
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
 */
metadata {
    definition (name: "alarm-panel", namespace: "hashneo", author: "Steven Taylor", cstHandler: true) {

		capability "Alarm"
		capability "Polling"
        capability "Refresh"

		command "off"
		command "home"
		command "away"

        attribute "events", "string"
		attribute "messages", "string"
		attribute "status", "string"

        command "updateStatus"
    }

    simulator {
        // TODO: define status and reply messages here
    }

	tiles(scale: 2) {
	    multiAttributeTile(name:"status", type: "generic", width: 6, height: 4){
    	    tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
                attributeState "OFF", label:'${name}', icon: "st.security.alarm.off", backgroundColor: "#505050"
                attributeState "HOME", label:'${name}', icon: "st.Home.home4", backgroundColor: "#00BEAC"
                attributeState "AWAY", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#008CC1"
                attributeState "pending off", label:'${name}', icon: "st.security.alarm.off", backgroundColor: "#ffffff"
                attributeState "pending away", label:'${name}', icon: "st.Home.home4", backgroundColor: "#ffffff"
                attributeState "pending home", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
                attributeState "AWAY_COUNT", label:'countdown', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
                attributeState "failed set", label:'error', icon: "st.secondary.refresh", backgroundColor: "#d44556"
                attributeState "alert", label:'${name}', icon: "st.alarm.beep.beep", backgroundColor: "#ffa81e"
                attributeState "alarm", label:'${name}', icon: "st.security.alarm.alarm", backgroundColor: "#d44556"
            }

            tileAttribute ("panel.status", width: 6, height: 2, key: "SECONDARY_CONTROL") {
                attributeState "status", label:'${currentValue}'
            }
        }

        standardTile("off", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state ("OFF", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#008CC1", nextState: "pending")
            state ("AWAY", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
            state ("HOME", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
            state ("pending", label:"pending", icon: "st.security.alarm.off", backgroundColor: "#ffffff")
        }

        standardTile("away", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state ("OFF", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending")
            state ("AWAY", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#008CC1", nextState: "pending")
            state ("HOME", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending")
            state ("pending", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
            state ("AWAY_COUNT", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
        }

        standardTile("home", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state ("OFF", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
            state ("AWAY", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
            state ("HOME", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#008CC1", nextState: "pending")
            state ("pending", label:"pending", icon: "st.Home.home4", backgroundColor: "#ffffff")
        }

        valueTile("events", "device.events", width: 6, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false, decoration: "flat", wordWrap: true) {
            state ("default", label:'${currentValue}')
        }

        main(["status"])
        details(["status","off", "away", "home", "events"])

    }
}

/*
def parse(String description) {
    log.debug "parse description: $description"

    def result = createEvent(name: attrName, value: attrValue)

    log.debug "Parse returned ${result?.descriptionText}"
    return result
}
*/

def updateStatus(Map status) {
    log.debug "updateStatus => '${status}'"

    if ( status.flags?.alarm ){
        sendEvent(name: "alarm", value: "both")
    } else {
        sendEvent(name: "alarm", value: "off")
    }

   	sendEvent(name: "panelStatus", value: status.message)
}

def checkState() {
	log.debug "checking state"
}

// handle commands
def off() {
	parent.call('off')
}

def home() {
	parent.call('home')
}

def away() {
	parent.call('away')
}

