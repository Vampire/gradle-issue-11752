plugins {
    groovy
}

repositories {
    mavenCentral()
}

testing {
    suites {
        val test by existing(JvmTestSuite::class) {
            useSpock("2.3-groovy-3.0")
            targets.all {
                testTask {
                    failFast = true
                }
            }
        }
    }
}

dependencies {
    implementation(gradleApi())
}
