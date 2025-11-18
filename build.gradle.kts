// build.gradle.kts (root)
plugins {
    kotlin("multiplatform") version "2.0.21"
}

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.web.widgets)
            }
        }
    }
}

compose {
    web {
        // এটা দরকার যাতে GitHub Pages-এ সঠিকভাবে লোড হয়
    }
}
