# Alpha Flash Select Kotlin Multiplatform Example

This is a sample client for Alpha Flash Select.
Currently supports: Android, iOS, Web, Desktop.

## Configuration

Service credentials should be placed into a file called 'credentials.properties', with a _username_ and
_password_ property set.

```properties
username=XXXXX
password=XXXX
```

## Running the Sample
This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop.

| Name    | Run                                  | 
|---------|--------------------------------------| 
| Android | .gradlew assemble                    | 
| iOS     | use XCode                            | 
| Desktop | .gradlew run                         | 
| Web     | .gradlew wasmJsBrowserDevelopmentRun | 

## Dependencies

| Name          | Purpose               | Links |
|---------------|-----------------------| -- |
| Serialization | kotlinx Serialization | [Source](https://github.com/Kotlin/kotlinx.serialization) |
| Ktor          | Http Requests         | [Website](https://github.com/ktorio/ktor) |


## Structure

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.