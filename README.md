# Konnected Vista Integrations

This set of device handler and smart app adds helpful functionality to your Konnected Alarm Interface and Vista series alarm panel.

These features are based upon the work by Dave Parsons in [Konnected Support thread #32000001630](https://help.konnected.io/support/discussions/topics/32000001630). 
Specifically the setup described in [Konnected Alarm Vista20p Setup.pdf](https://help.konnected.io/helpdesk/attachments/32004430083).

_**Discrepancy**_

Be sure to add a 2 KΩ as an EOL resistor across the key switch zone as per [Vista 20PS Series Setup](https://www.manualslib.com/manual/847525/Ademco-Vista-20ps-Series.html?page=16). Program the zone as an EOL hardware type, not Normally Open.

## Panel Programming

`*80` Output Functions

A typical way to configure the alarm output functions. These will be normally open, but stay closed because of action `2` until the system is disarmed.

| Function # | Activated By | Zone Type | Partition # | Action | Output # | What |
| ---------- | ------------ | --------- | ----------- | ------ | -------- | ---- |
| 01 | 2 | 21 | 0 | 2 | 18 | Armed Away |
| 02 | 2 | 22 | 0 | 0 | 18 | Disarmed |
| 03 | 2 | 20 | 0 | 2 | 17 | Armed Stay |
| 04 | 2 | 22 | 0 | 0 | 17 | Disarmed |

I recommend additionally setting the field `*84` to `3` to override auto-stay.

## SmartThings Setup

When discovering a new device in the "Konnected (Connect)" smart app, I suggest initially choosing "Konnected Momentary Switch" for the key switch relay. You will change this later. 

Install the device handler "Konnected Key Switch Relay". In the SmartThings IDE, replace the the device type of your key switch relay with the new device handler. This will give you the functionality to either push (default 1s) or hold (default 3s) the key switch relay triggering the away and stay modes respectively.

Next it's time to install the "Konnected Vista Integrations" Smart App. After publishing, you'll be able to add it using the mobile app. Choose the key switch relay above and the two sensors you have attached to the Vista output pins.

You can now use the Smart Home Monitor to arm and disarm your Vista alarm system. It will also modify its state if you arm and disarm via the keypad.
