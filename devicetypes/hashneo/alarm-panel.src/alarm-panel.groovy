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

        command "updateStatus"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        valueTile("display", "panel.display", width: 2, height: 2, canChangeIcon: false) {
            label: '${panelValue}'
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
   	sendEvent(name: "panelValue", value: status.message, display: true, displayed: true)
}

def checkState() {
	log.debug "checking state"
}