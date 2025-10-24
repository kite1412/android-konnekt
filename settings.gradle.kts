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
include(":core:data")
include(":core:designsystem")
include(":core:domain")
include(":core:media")
include(":core:model")
include(":core:network:supabase")
include(":core:network:upload:domain")
include(":core:network:upload:util")
include(":core:storage:file")
include(":core:ui")
include(":feature:authentication")
include(":feature:chatdetail")
include(":feature:chats")
include(":feature:conversation")
