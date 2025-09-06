# Konnekt - Android Chat Application

## Server
Read the [Supabase setup](/core/network/supabase/README.md) guide to configure the default Supabase server, or use a custom server by implementing classes in the [core/domain](/core/domain) module within a new module under [core/network](/core/network) module, then add it as a dependency in the [core/data](/core/data) Gradle build file.
```kotlin
dependencies {
    implementation(projects.konnekt.core.network.<your module name>)
}
```
Remove the default Supabase implementation:
```kotlin
implementation(projects.konnekt.core.network.supabase)
```
