/**
 *  sensor-carbonMonoxide
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
    definition (name: "sensor-co2", namespace: "hashneo", author: "Steven Taylor", cstHandler: true) {
        capability "Battery"
        capability "Carbon Monoxide Detector"

        command "updateStatus"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"zone", type: "generic", width: 6, height: 4){
            tileAttribute ("device.carbonMonoxide", key: "PRIMARY_CONTROL") {
                attributeState "clear", label:'no co2', icon:"st.alarm.carbon-monoxide.clear", backgroundColor:"#ffffff"
                attributeState "detected", label:'co2', icon:"st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor:"#53a7c0"
            }
        }
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

    def newState = status.tripped.current ? "detected" : "clear"
    def desc = status.tripped.current ? "CO2 Detected" : "CO2 Cleared"

    sendEvent(name: "carbonMonoxide", value: "${newState}", descriptionText: "${desc}")
   	sendEvent(name: "battery", value: status.battery?.level, display: true, displayed: true)
}

def checkState() {
	log.debug "checking state"
    sendEvent (name: "carbonMonoxide", value: "clear")
}