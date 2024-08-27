---
sidebar_position: 1
---

# First Android App

This section will guide you through the installing [Android Studio][1] and builds a simple Android app. We want to make sure your system is running and able to build Android apps. If your system is already setup for Android development, please feel free to [skip](workshop-server.md) this section.

## Installing Android Studio

Android Studio is the preferred development environment (sometimes referred to as an IDE, i.e. Integrated Development Environment) for developing Android apps. The most up-to-date information on how to install it can be found on the main developer page [of Android Studio][1], here you will find a snapshot for your convenience.

## Downloading Android Studio

At the time of writing, `Koala` is the newest version of Android Studio, so please follow this link 

https://developer.android.com/studio

and continue to `Download Android Studio Koala`.

Once you accepted the terms and conditions, you might face a specific for which version to download.

<img
src={require('/img/mobile/android/install-cpu-selection.png').default}
alt="Android Studio: Which CPU"
style={{ width: "50%" }}
/>

Please select the appropriate version and continue to download Android Studio.

Once the download is complete, you can install Android Studio by finding it in the folder you downloaded it to. A double click on the symbol will start the installation.

Depending on your operating system, you now need to follow the onscreen instructions.

<img
src={require('/img/mobile/android/install-move.png').default}
alt="Android Studio: Install to Move."
style={{ width: "50%" }}
/>

For mac this is moving the symbol into the `Applications` folder as depicted above.

Once Android Studio is installed, please start it to welcome the setup wizard.

## Starting Android Studio for the First Time

After again following the prompt from Android Studio, you need to restart Android Studio one more time.

After the final restart, you are greeted with a new project wizard:

<img
    src={require('/img/mobile/android/simple-project-template.png').default}
    alt="Android Studio: Menu new project."
    style={{ width: "50%" }}
/>

Please select `Empty Activity` with the rotated isometric cube in the middle, the second image in the wizard and highlighted in the screenshot. The cube is the logo for Jetpack Compose and will be used as the framework to render our user interfaces.


Next we got to configure the new project. The following screenshot shows the updated configuration. Please enter the following information:

* `Name` the name of the app. For consistency, we recommend naming the app `PawsKey`, a joke on the term `Passkeys` and our love for paws.
* `Package Name` a name of a package, it should be including your company and your name of the app. Ideally in [Reverse domain name notation](https://en.wikipedia.org/wiki/Reverse_domain_name_notation). Similar to `io.yubicolabs.pawskey`.
* `Save Location` is the location on your computer where to store the source code.
* `Minimum SDK` the smallest version number of the Android SDK supported. Feel free to explore further by clicking on the `help me choose` link to find the right version. We are going with `API 24`.
* and finally the `Build configuration language`: We are using `kotlin` for this example, and recommend you to do the same.

All those settings are applied already in the following screenshot.

<img
    src={require('/img/mobile/android/simple-project-config.png').default}
    alt="Android Studio: Menu new project."
    style={{ width: "50%" }}
/>

## Building the First App

The final step for configuring your machine to build an Android app is to actually build an Android app.

Please click on the little play icon in the top left corner of Android Studio, next to the `app` name of your Android app. This tells Android Studio to actually build the app and run it in an emulator.

<img
src={require('/img/mobile/android/simple-project-first-run.png').default}
alt="Android Studio: Menu new project."
style={{ width: "25%" }}
/>

If everything went smoothly, you should see a window similar to the following screenshot:

<img
src={require('/img/mobile/android/simple-project-first-app.png').default}
alt="Android Studio: Menu new project."
style={{ width: "50%" }}
/>

Take note of the newly run app in the emulator on the right side of the screen: White background and a text reading `Hello World`.


## Next Steps

Now that you can build a simple Android app, congratulations, the [Next section](workshop-server.md) will explain how to run the workshop backend called relying party, so we can connect the app to it.

[1]: https://developer.android.com/studio
