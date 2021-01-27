/**
 *  sensor-motion
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
    definition (name: "sensor-motion", namespace: "hashneo", author: "Steven Taylor", cstHandler: true) {
        capability "Battery"
        capability "Motion Sensor"

        command "updateStatus"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"zone", type: "generic", width: 6, height: 4){
            tileAttribute ("device.motion", key: "PRIMARY_CONTROL") {
                attributeState "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
                attributeState "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"
            }
        }
    }
}

def updateStatus(Map status) {
    log.debug "updateStatus => '${status}'"

    def newState = status.motion.tripped.current ? "active" : "inactive"
    def desc = status.motion.tripped.current ? "Detected Motion" : "Motion Has Stopped"

    sendEvent (name: "motion", value: "${newState}", descriptionText: "${desc}")
   	sendEvent(name: "battery", value: status.battery?.level, display: true, displayed: true)
}

def checkState() {
	log.debug "checking state"
    sendEvent (name: "motion", value: "inactive")
}