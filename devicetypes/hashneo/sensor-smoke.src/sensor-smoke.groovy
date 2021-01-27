/**
 *  sensor-smoke
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
    definition (name: "sensor-smoke", namespace: "hashneo", author: "Steven Taylor", cstHandler: true) {
        capability "Battery"
        capability "Smoke Detector"

        command "updateStatus"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"zone", type: "generic", width: 6, height: 4){
            tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
                attributeState "clear", label:'no smoke', icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff"
                attributeState "detected", label:'smoke', icon:"st.alarm.smoke.smoke", backgroundColor:"#53a7c0"
            }
        }
    }
}

def updateStatus(Map status) {
    log.debug "updateStatus => '${status}'"

    def newState = status.smoke.tripped.current ? "detected" : "clear"
    def desc = status.smoke.tripped.current ? "Smoke Detected" : "Smoke Cleared"

    sendEvent(name: "smoke", value: "${newState}", descriptionText: "${desc}")
   	sendEvent(name: "battery", value: status.battery?.level, display: true, displayed: true)
}

def checkState() {
	log.debug "checking state"
    sendEvent (name: "smoke", value: "clear")
}