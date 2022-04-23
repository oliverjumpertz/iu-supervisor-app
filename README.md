# Supervisor-App

This app was created as part of the study *Projekt Mobile Software Engineering (IWMB02)* for the IUBH informatic econmics bachelor degree program. It is intended to serve supervisors and students of the IUBH for the communication regarding final theses.

## Features 💡

- There is a list of all supervisors and their research areas, which can be read by the user role Student.
- There is a form for posting theses, which can be used by the user role Supervisor. (+ Mitigation of erroneous data through input validation).
- There is a form through which messages can be sent to supervisors, which can be used by the user role Student. (+ mitigation of erroneous data through input validation).
- There is a listing of all papers assigned to the logged in supervisor. The papers show one of the statuses: in coordination, registered, submitted, colloquium held.
- The user role supervisor can assign another supervisor as second supervisor to the papers in a dropdown menu.
- The user role supervisor can see all papers in a separate overview as in requirement D, but only explicitly for the papers for which he is second supervisor.
- The user role will be shown in the papers in which they are reviewer or second reviewer, whether the payment of - the invoice for the supervision of the paper has already been made. The statuses can be: submitted, paid

## Usage 🔧

1. Build the app locally (there is no released APK. Sorry.)
2. open with [Adroid Studio](https://developer.android.com/studio?hl=de&gclid=CjwKCAjwoduRBhA4EiwACL5RPzPoqmVauzQiHXuMqgo9KJwbawVOsrknG0Obk6y5k1NOHb8nQkjXIhoCiQYQAvD_BwE&gclsrc=aw.ds) or an Android device.

2.1 Android Studio:
- Launch the emulator with an Android version 5.0 or higher
- It's best to use a Google Play Store image as it comes pre-installed with a PDF viewer, which you will need to use all functionality of this app
  - You can, for example use the Nexus 5 Google Play image with an API version of 24
- Install the app on the emulated device.

2.2 Android device:
- Install the app on the device
- Launch the app in *Programs*.
- Use the app

3. log in with the provided student or supervisor account (see available usernames under "Accounts")
4. You will want to have a PDF available for "upload". If you are missing one, use the "Testdokument.pdf" in this repository and upload it to your emulator or device.

## Dependencies 🔗

Listed here are all dependencies that exceed the usual dependencies pre-installed
by Android studio when creating a new project.

- androidx.room
- io.reactivex.rxjava3

## Accounts

The app has three accounts pre-defined. These are:

- betreuereins
- betreuerzwei
- student

They should be enough to experience the full feature set of the app.

## Important

The app itself has **no** remote logic implemented. Everything is mocked
locally and depends on a SQLite database. All state will be persisted but
is not carried over to other devices!

## Issues ⚠

If you find any issues, please feel free to leave a comment!

(Icons Source: https://unicode.org/emoji/charts/full-emoji-list.html)
