= README =

== Project Setup (required for Maven build) == 

Environment Variables: 

Linux: export ANDROID_HOME="PATH/TO/THE/ANDROID/SDK"

Windows:
Start -> Systemsteuerung -> System -> Erweiterte Systemeinstellungen
-> Umgebungsvariablen -> Neu
  Name: ANDROID_HOME
  Wert: C:\path\to\android\sdk

Path for executables:
Linux: Add $ANDROID_HOME/tools and $ANDROID_HOME/platform-tools to $PATH
Windows: Add %ANDROID_HOME%\tools and %ANDROID_HOME%\platform-tools to %PATH%  

Note: On Windows, you have to restart Eclipse and/or start a new command prompt
for these changes to take effect.

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
 
== [DEPRECATED?] Structure of project and test project ==

According to 

http://developer.android.com/guide/topics/testing/testing_android.html

it is a best practice to include the test project into the main project,
although it is a separate Eclipse project. Thus, the folder /tests in the main
project contains another Eclipse project, with a separate .project and
.classpath file which should be imported as a project of its own in your
Eclipse workspace. Of course, when browsing the directory /tests in the main
project, you see the very same files as when browsing the test project.