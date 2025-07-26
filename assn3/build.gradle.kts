plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.9")
    implementation("io.ktor:ktor-server-core-jvm:2.3.9")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.9")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.9")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
    implementation("io.ktor:ktor-server-cors:2.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.ktor:ktor-server-status-pages:2.3.9")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests:2.3.9")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.9")
    testImplementation("io.ktor:ktor-client-core:2.3.9")
    testImplementation("io.ktor:ktor-client-cio:2.3.9")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}