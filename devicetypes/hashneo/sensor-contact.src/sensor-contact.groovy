/**
 *  sensor-contact
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
    definition (name: "sensor-contact", namespace: "hashneo", author: "Steven Taylor", cstHandler: true) {
        capability "Battery"
        capability "Motion Sensor"
        capability "Sensor"

        command "updateStatus"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"zone", type: "generic", width: 6, height: 4){
            tileAttribute ("device.contact", key: "PRIMARY_CONTROL") {
                attributeState "inactive", label:'no contact', icon:"st.contact.contact.inactive", backgroundColor:"#ffffff"
                attributeState "active", label:'contact', icon:"st.contact.contact.active", backgroundColor:"#53a7c0"
            }
        }

        main "updateStatus"

        details(["updateStatus"])
    }
}

def updateStatus(Map status) {

    log.debug "updateStatus => '${status}'"
    // need to convert open to active and closed to inactive

    def newState = status.tripped.current ? "open" : "closed"

    def desc = status.tripped.current ? "Contact Open" : "Contact Closed"

    sendEvent(name: "contact", value: "${newState}", descriptionText: "${desc}")
    
   	sendEvent(name: "battery", value: status.battery.level, display: true, displayed: true)
}