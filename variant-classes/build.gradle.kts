plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("org.jetbrains.dokka") version "1.7.20"
}

group = "org.itmo.variant"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    "implementation"(project(":collections"))
    "implementation"("org.valiktor:valiktor-core:0.12.0")
    "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib")
    "testImplementation"("org.jetbrains.kotlin:kotlin-test")
    "testImplementation"("io.mockk:mockk:1.13.4")
}

tasks.compileKotlin{
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
