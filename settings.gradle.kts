rootProject.name = "Calendar"
include(":sample:composeApp")
include(":calendar")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
 		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }

}
