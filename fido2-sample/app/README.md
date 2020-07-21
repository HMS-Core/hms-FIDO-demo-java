## FIDO2 Sample


## Table of Contents

 * [Introduction](#introduction)
 * [Getting Started](#getting-started)
 * [Installation](#installation)
 * [Configuration ](#configuration )
 * [Supported Environments](#supported-environments)
 * [Sample Code](#sample-code)
 * [License](#license)


## Introduction
    FIDO2 Sample provides many sample programs for your reference or usage.

## Getting Started
	For more development details, please refer to the following link:

	Development Guide: https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/FIDO2_Overview

	API References: https://developer.huawei.com/consumer/en/doc/development/HMS-References/FIDO2Packagecomhuaweihmssupportapifidofido2



	We also provide an example to demonstrate the use of FIDO2 SDK for Android.

	This sample uses the Gradle build system.

	First download the demo by cloning this repository or downloading an archived snapshot.

	In Android Studio, use the "Open an existing Android Studio project", and select the directory of "fido2-sample".

	You should create an app in AppGallery Connect, and obtain the file of agconnect-services.json and add to the project. You should also generate a signing certificate fingerprint and add the certificate file to the project, and add configuration to build.gradle. See the [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/hms-map-configuringinagc) guide to configure app in AppGallery Connect.

	Replace "com.huawei.hms.fido.sample.fido2" in build.gradle with you own applicationId.

	You can use the "gradlew build" command to build the project

## Installation
    Before using FIDO2 Sample code, check whether the java environment and Android Studio have been installed.
    Decompress the FIDO2 Sample code package.

## Supported Environments
	Java 1.7 or a later version is recommended.

## Configuration
    NA


## Sample Code

FIDO2 includes two operations: registration and authentication. The processes are similar for the two operations.
    1). Create an activity.

    2). Obtain the challenge value and related policy from the FIDO server, and initiate a request.

    3). Call Fido2Client.getRegistrationIntent() to initiate registration, or call Fido2Client.getAuthenticationIntent() to initiate authentication.

    4). Call Fido2Intent.launchFido2Activity() in the callback to start registration (requestCode is Fido2Client.REGISTRATION_REQUEST) or authentication (requestCode is Fido2Client.AUTHENTICATION_REQUEST). The callback will be executed in the main thread.

    5). Call Fido2Client.getFido2RegistrationResponse() or Fido2Client.getFido2AuthenticationResponse() in the callback Activity.onActivityResult() to obtain the registration or authentication result.

    6). Send the registration or authentication result to the FIDO server for verification.



##  License
    FIDO2 Sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
