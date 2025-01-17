import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
	id("com.github.ben-manes.versions") version "0.51.0"
}

group = "com.traanite"
version = "1.2.2" // todo replace with property

java {
	sourceCompatibility = JavaVersion.VERSION_23
}

repositories {
	mavenCentral()
}

dependencies {
	val resilience4jVersion = "2.2.0"
	val jsoupVersion = "1.18.3"
	val guavaVersion = "33.4.0-jre"
	val springDocOpenApiVersion = "2.7.0"
	val slf4jVersion = "2.0.16"
	val kotlinLoggingJvmVersion = "7.0.3"

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("io.github.resilience4j:resilience4j-reactor:${resilience4jVersion}")
	implementation("io.github.resilience4j:resilience4j-ratelimiter:${resilience4jVersion}")
	implementation("org.jsoup:jsoup:${jsoupVersion}")
	implementation("com.google.guava:guava:${guavaVersion}")
	api("org.springdoc:springdoc-openapi-starter-webflux-ui:${springDocOpenApiVersion}")

	implementation("org.slf4j:slf4j-api:${slf4jVersion}")
	implementation("io.github.oshai:kotlin-logging-jvm:${kotlinLoggingJvmVersion}")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinJvmCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_23)
		freeCompilerArgs.add("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
