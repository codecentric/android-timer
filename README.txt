= README =

== Project Setup == 

To work on this project, you need to have Eclipse and the Android Development
Toolkit installed. You also need to have cloned the full repository from github
(git@github.com:codecentric/android-timer.git, see https://github.com/codecentric/android-timer)
including this project (android-timer) and the supporting project named "wheel".
Both projects also need to be imported into your Eclipse workspace as Android
projects. 

== How to use notification sounds and ringtones in the Android emulator ==

The emulator for Android comes without any notification sounds and ringtones.
To enable sounds in the emulator you have to copy them to the virtual device
first. 

* Start the emulator
* Go to the DDMS perspective
* Open the File Explorer tab
* Use the green plus symbol repeatedly to create the following directory structure in /sdcard:

/sdcard
   media
     audio
       alarms
       notifications
       ringtones

* Use the push symbol (arrow pointing towards mobile phone) to push some of the
  files from misc/media/audio/notifications into the notifications folder
  on the virtual device's sdcard.
* In the emulator go to 
  Menu -> Settings -> SD card & phone storage and click "Unmount SD card"
  (maybe not neccessary, don't know)
* Restart emulator (this also remounts the SD card)
* Menu -> Settings -> Sound & display -> Notification ringtone
* The new sounds should be there. Switch from "silent" to a different sound.