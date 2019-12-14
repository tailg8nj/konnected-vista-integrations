/**
 *  Konnected Vista Integrations
 *
 *  Copyright 2019 Peter Babinski
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
definition(
    name: "Konnected Vista Integrations",
    namespace: "tailg8nj",
    author: "Peter Babinski",
    description: "Konnected Vista Integrations",
    category: "Safety & Security",
    iconUrl:     "https://raw.githubusercontent.com/konnected-io/docs/master/assets/images/KonnectedSecurity.png",
    iconX2Url:   "https://raw.githubusercontent.com/konnected-io/docs/master/assets/images/KonnectedSecurity@2x.png",
    iconX3Url:   "https://raw.githubusercontent.com/konnected-io/docs/master/assets/images/KonnectedSecurity@3x.png"
)


preferences {
    section("Armed Stay Sensor") {
        input "armedStaySensor", "capability.contactSensor", title: "Select stay sensor", required: true, multiple: false
    }
    section("Armed Away Sensor") {
        input "armedAwaySensor", "capability.contactSensor", title: "Select away sensor", required: true, multiple: false
    }
    section("Key Switch Relay") {
        input "keySwitchRelay", "device.konnectedKeySwitchRelay", title: "Select key switch relay", required: true, multiple: false
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def getVistaState() {
    if(armedStaySensor.currentState("contact")?.value == "closed") {
    	return "stay"
    } else if (armedAwaySensor.currentState("contact")?.value == "closed") {
    	return "away"
    } else {
    	return "off"
    }
}

def initialize() {
	def oldState = location.currentState("alarmSystemStatus")?.value
    def newState = getVistaState()
    log.debug "Initial SHM status ${oldState} vs Vista status ${newState}"
    syncAlarmSystemStatus(oldState, newState)
    subscribe(location, "alarmSystemStatus", shmHandler)
    subscribe(armedStaySensor, "contact", vistaHandler)
    subscribe(armedAwaySensor, "contact", vistaHandler)
}

def shmHandler(evt) {
  def oldState = getVistaState()
  def newState = evt.value
  log.debug "Updated SHM status ${newState} vs Vista status ${oldState}"
  if (!newState.equals(oldState)) {
    if (newState.equals("away") || newState.equals("off")) {
      log.debug "Pushing key switch"
      keySwitchRelay.push()
    } else if (newState.equals("stay")) {
      log.debug "Holding key switch"
      keySwitchRelay.hold()
    }
  }
}

def syncAlarmSystemStatus(oldState, newState) {
    if(!oldState.equals(newState)) {
      log.debug "Changing SHM status to ${newState}"
      sendLocationEvent(name: "alarmSystemStatus", value: newState)
    }
}

def vistaHandler(evt) {
  def oldState = location.currentState("alarmSystemStatus")?.value
  def newState = getVistaState()
  log.debug "Updated Vista status ${newState} vs SHM status ${oldState}"
  syncAlarmSystemStatus(oldState, newState)
}
