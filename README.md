# Konnected Vista Integrations

This set of device handler and smart app adds helpful functionality to your Konnected Alarm Interface and Vista series alarm panel.

## Installing

These features are based upon the work by Dave Parsons in https://help.konnected.io/support/discussions/topics/32000001630. Specifically the setup described in [Konnected Alarm Vista20p Setup.pdf](https://help.konnected.io/helpdesk/attachments/32004430083).

After getting your Konnect Alarm Interface working with the default "Konnect Momentary Switch" for the key switch relay, Install the device handler "Konnected Key Switch Relay". In the SmartThings IDE, replace the the device type of your key switch with the  new device handler. This will give you the functionality to either push (default 1s) or hold (default 3s) the key switch relay triggering the away and stay modes respectively.

Next it's time to install the "Konnectd Vista Integrations" Smart App. After publishing, you'll be able to add it using the mobile app. Choose the key switch relay above and the two sensors you have attached to the Vista output pins.

You can now use the Smart Home Monitor to arm and disarm your Vista alarm system. It will also modify its state if you arm and disarm via the keypad.
