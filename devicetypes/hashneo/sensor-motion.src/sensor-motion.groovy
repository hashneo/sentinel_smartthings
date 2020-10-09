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
    definition (name: "Sentinel Motion Sensor", namespace: "hashneo", author: "Steven Taylor", cstHandler: true) {
        capability "Battery"
        capability "Motion Sensor"
        capability "Sensor"

        command "zone"
    }


    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"zone", type: "generic", width: 6, height: 4){
            tileAttribute ("device.motion", key: "PRIMARY_CONTROL") {
                attributeState "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
                attributeState "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"
                attributeState "alarm", label:'ALARM', icon:"st.motion.motion.active", backgroundColor:"#ff0000"
            }
        }

        main "zone"

        details(["zone"])
    }
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
    // TODO: handle 'battery' attribute
    // TODO: handle 'motion' attribute

}

def zone(String state) {
    // need to convert open to active and closed to inactive
    def eventMap = [
        'closed':"inactive",
        'open':"active",
        'alarm':"alarm"
    ]

    def newState = eventMap."${state}"

    def descMap = [
        'closed':"Motion Has Stopped",
        'open':"Detected Motion",
        'alarm':"Alarm Triggered"
    ]

    def desc = descMap."${state}"

    sendEvent (name: "motion", value: "${newState}", descriptionText: "${desc}")
}