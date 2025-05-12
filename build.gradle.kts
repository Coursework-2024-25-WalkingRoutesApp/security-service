plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.hse"
version = "1.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val kotlinVersion: String by project
val kotlinTestJunit5Version: String by project
val springBootStarterVersion: String by project
val springCloudVersion: String by project
val springSecurityTestVersion: String by project
val springKafkaVersion: String by project
val springKafkaTestVersion: String by project
val jacksonModuleKotlinVersion: String by project
val jjwtVersion: String by project
val junitPlatformLauncherVersion: String by project
val micrometerJvmExtrasVersion: String by project
val springDocVersion: String by project

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-security:$springBootStarterVersion")
	implementation("org.springframework.boot:spring-boot-starter-web:$springBootStarterVersion")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")

	implementation("io.jsonwebtoken:jjwt:$jjwtVersion")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModuleKotlinVersion")

	implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.kafka:spring-kafka:$springKafkaVersion")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-core")
	implementation("io.github.mweirauch:micrometer-jvm-extras:$micrometerJvmExtrasVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootStarterVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinTestJunit5Version")
	testImplementation("org.springframework.security:spring-security-test:$springSecurityTestVersion")
	testImplementation("org.springframework.kafka:spring-kafka-test:$springKafkaTestVersion")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformLauncherVersion")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
