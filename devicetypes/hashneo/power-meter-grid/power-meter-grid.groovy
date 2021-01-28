/**
 *  power-meter-grid
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
    definition (name: "power-meter-grid", namespace: "hashneo", author: "Steven Taylor", cstHandler: true) {
        capability "Energy Meter"
        capability "Power Meter"
        capability "Sensor"

        command "updateStatus"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"power", type: "generic", width: 6, height: 4){
			tileAttribute("device.power", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue} W')
			}
			tileAttribute("device.energy", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'${currentValue} kWh')
			}
        }

        main (["power","energy"])
    }
}

def updateStatus(Map status) {

    def gridIn  = Float.parseFloat( status.grid.in );
    def gridOut = Float.parseFloat( status.grid.out );

	if ( !status.demand.equals("nan") ){
        log.debug "updateStatus => '${status}'"

        sendEvent(name: "energy", value: (gridIn-gridOut), unit: 'kWh')
        sendEvent(name: "power", value: status.demand, unit: "kWh")
    }
}

def checkState() {
	log.debug "checking state"
}