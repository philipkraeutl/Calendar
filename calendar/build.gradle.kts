import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.cli.common.toBooleanLenient
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.multiplatform)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.compose)
	alias(libs.plugins.android.library)
	alias(libs.plugins.ktlint)
	id("com.vanniktech.maven.publish") version "0.27.0"
}

fun shouldPublish() = System.getenv("PUBLISH")?.toBooleanLenient() == true

fun getEnvValue(name: String) = System.getenv(name) ?: throw Exception("$name not given")

publishing {
	if (!shouldPublish()) return@publishing
	repositories {
		maven {
			url = uri(getEnvValue("REGISTRY_URL"))
			credentials {
				username = getEnvValue("REGISTRY_USERNAME")
				password = getEnvValue("REGISTRY_PASSWORD")
			}
		}
	}
}

mavenPublishing {
	if (!shouldPublish()) return@mavenPublishing
	coordinates("com.swapindo", "calendar", "1.1.0")

	pom {
		name.set(project.name)
		description.set("Calendar for Swapindo.")
		inceptionYear.set("2024")
		scm {
			val projectLocation = "github.com/${getEnvValue("GITHUB_REPOSITORY")}"

			url.set("https://$projectLocation")
			connection.set("scm:git:git://$projectLocation.git")
			developerConnection.set("scm:git:ssh://git@$projectLocation.git")
		}
	}
}

kotlin {
	androidTarget {
		publishLibraryVariants("release")
		compilations.all {
			compileTaskProvider {
				compilerOptions {
					jvmTarget.set(JvmTarget.JVM_1_8)
					freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_1_8}")
				}
			}
		}
	}

	jvm()

	js {
		browser()
		binaries.executable()
	}

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64(),
	).forEach {
		it.binaries.framework {
			baseName = "ComposeApp"
			isStatic = false
		}
	}

	sourceSets {
		all {
			languageSettings {
				optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
			}
		}
		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(compose.material3)
			implementation(compose.materialIconsExtended)
			@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
			implementation(compose.components.resources)
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.composeIcons.featherIcons)
			api(libs.kotlinx.datetime)
		}

		androidMain.dependencies {
			implementation(libs.androidx.appcompat)
			implementation(libs.androidx.activityCompose)
			implementation(libs.compose.uitooling)
			implementation(libs.kotlinx.coroutines.android)
		}

		jvmMain.dependencies {
			implementation(compose.desktop.common)
			implementation(compose.desktop.currentOs)
			implementation(libs.kotlinx.coroutines.swing)
		}

		jsMain.dependencies {
			implementation(compose.html.core)
		}

		iosMain.dependencies {
		}
	}
}


android {
	namespace = "com.wojciechosak.calendar"
	compileSdk = 34

	defaultConfig {
		minSdk = 24
	}
	sourceSets["main"].apply {
		manifest.srcFile("src/androidMain/AndroidManifest.xml")
		res.srcDirs("src/androidMain/resources")
		resources.srcDirs("src/commonMain/resources")
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.compose.asProvider().get()
	}
	kotlin {
		jvmToolchain(17)
	}
}

compose.desktop {
	application {
		mainClass = "MainKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "com.wojciechosak.calendar.desktopApp"
			packageVersion = "1.0.0"
		}
	}
}

compose.experimental {
	web.application {}
}

