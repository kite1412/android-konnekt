pluginManagement {
    includeBuild("buildlogic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Konnekt"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") // enable access to other modules using projects.*
include(":app")
include(":core:designsystem")
include(":core:model")
include(":core:data")
include(":core:domain")
include(":core:network:supabase")
include(":feature:authentication")
include(":feature:chats")
include(":core:ui")
