# App Installer

An automation apk that installs other apps and configures a phone e.g. adding a Google account
using Android Instrumentation testing framework.

## Instructions

Build the artifacts, automator and checker apks. These are placed in the installer engine's 
artifact directory to be used in the automation process. Remember to change your app's package name
in *InstallAutomator.apk* file.

## Build

To generate the automator and checker apks, run the **exportArtifacts** gradle custom task. Refer
to task for details. 