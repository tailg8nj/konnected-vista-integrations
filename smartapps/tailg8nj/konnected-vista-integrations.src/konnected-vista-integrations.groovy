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

def initialize() {
    atomicState.alarmSystemStatus = location.currentState("alarmSystemStatus")?.value
    subscribe(location, "alarmSystemStatus", alarmHandler)
    subscribe(armedStaySensor, "contact", statusHandler)
    subscribe(armedAwaySensor, "contact", statusHandler)
}

def alarmHandler(evt) {
  def oldState = atomicState.alarmSystemStatus
  def newState = evt.value
  log.debug "Alarm handler state ${oldState} vs value ${newState}"
  if (!newState.equals(oldState)) {
    if (newState.equals("away") || newState.equals("off")) {
      log.debug "pushing key switch"
      keySwitchRelay.push()
    } else if (newState.equals("stay")) {
      log.debug "holding key switch"
      keySwitchRelay.hold()
    }
    atomicState.alarmSystemStatus = newState
  }
}

def statusHandler(evt){
  def oldState = state.alarmSystemStatus
  log.debug "Event from ${evt.device} value ${evt.value} with state ${oldState}"
  if (armedStaySensor.id.equals(evt.deviceId)) {
    if("open".equals(evt.value) && !"off".equals(oldState)) {
      log.debug "Changing alarm status to off"
      sendLocationEvent(name: "alarmSystemStatus", value: "off")
    } else if ("closed".equals(evt.value) && "off".equals(oldState)) {
      log.debug "Changing alarm status to stay"
      sendLocationEvent(name: "alarmSystemStatus", value: "stay")
    }
  } else if (armedAwaySensor.id.equals(evt.deviceId)) {
    if("open".equals(evt.value) && !"off".equals(oldState)) {
      log.debug "Changing alarm status to off"
      sendLocationEvent(name: "alarmSystemStatus", value: "off")
    } else if ("closed".equals(evt.value) && "off".equals(oldState)) {
      log.debug "Changing alarm status to away"
      sendLocationEvent(name: "alarmSystemStatus", value: "away")
    }  
  }
}
