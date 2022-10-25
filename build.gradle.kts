import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.*

plugins {
    kotlin("multiplatform") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.0"
    id("com.squareup.sqldelight") version "1.5.4"
    id("com.codingfeline.buildkonfig") version "0.13.3"
}

group = "to.sava.savatter"
version = "0.0.1"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io/")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.materialIconsExtended)
                implementation(compose.uiTooling)
                implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
                implementation(kotlin("stdlib-jdk8"))

                implementation("net.harawata:appdirs:1.2.1")
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.4")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.5.4")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "Savatter"
            packageVersion = "0.0.1"
        }
    }
}

sqldelight {
    database("Storage") {
        packageName = "to.sava.savatter"
    }
}

buildkonfig {
    packageName = "to.sava.savatter.config"

    defaultConfigs {
        val props = Properties().apply {
            project.rootProject.file("local.properties").let {
                if (it.exists()) {
                    load(it.inputStream())
                }
            }
        }
        for (key in listOf(
            "twitterConsumerKey",
            "twitterConsumerSecret",
            "twitterClientId",
            "twitterClientSecret",
        )) {
            buildConfigField(FieldSpec.Type.STRING, key, props.getProperty(key, key))
        }
    }
    targetConfigs {
        create("jvm") {
            buildConfigField(FieldSpec.Type.STRING, "hogeName2", "jvm")
        }
    }
}
