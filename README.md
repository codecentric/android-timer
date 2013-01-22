codecentric android timer
=========================

This Android app is a simple countdown timer. You can set the timer (hours, minutes & seconds) and kick it off. Once started the timer will count down. When the timer reaches zero, it will start beeping. That's it, mostly.

In detail, the app offers a few more features:

* Two different styles to set the timer:
    * simple text fields or
    * wheels (similar to slot machine reels, credit goes to the [android-wheel project](http://code.google.com/p/android-wheel/))
* Two different visualizations for a running countdown:
    * display remaining time like a digital clock,
    * visualize remaining time versus total time as a pie chart or
    * combine both of the above
* Option to keep the display turned on during the whole countdown

Currently, this app is not available on Google's Play Store, but you can download the [latest version](https://github.com/codecentric/android-timer/raw/master/android-timer/release/android-timer.apk) of it from the [release directory](https://github.com/codecentric/android-timer/tree/master/android-timer/release) in the repository.

Contributing
------------

To work on this project, you need to have Eclipse and the Android Development
Toolkit installed. You also need to have cloned the full repository from github
(git@github.com:codecentric/android-timer.git, see
https://github.com/codecentric/android-timer).

This includes three projects:
* android-timer
* android-timer-test
* wheel

All three projects need to be imported into your Eclipse workspace.
.poject and .classpath files are provided.

Some class path variables need to be set up:

* ANDROID_SDK
    * should point to the root directory of your android SDK
    * needs to contain platform level 7
* ROBOLECTRIC
    * should point to a directory where the robolectric jars reside
    * needs to contain files named
        * robolectric-1.2-SNAPSHOT-jar-with-dependencies.jar
        * robolectric-1.2-SNAPSHOT-sources.jar
* JUNIT
* HAMCREST-CORE
* HAMCREST-LIBRARY
* MOCKITO-ALL

*** Building the APK

You need a keystore with a developer certificate to sign the APK.

In Eclipse:
* File - Export - Android - Export Android Application
* Project: android-timer
* Configure the keystore to use
* Destination APK file: <your-project-home>/android-timer/release/android-timer.apk
* Finish
