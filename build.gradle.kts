plugins {
    kotlin("jvm") version "1.5.10"
}

group = "zmj.test"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://maven.aliyun.com/repository/google") }
    maven { setUrl("https://maven.aliyun.com/repository/jcenter") }
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("scripting-jsr223"))

    implementation("com.github.javaparser:javaparser-core:3.23.0")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.23.0")

    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("org.jdom:jdom:2.0.2")
}