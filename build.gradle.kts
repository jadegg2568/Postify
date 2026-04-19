plugins {
	java
	id("org.springframework.boot") version "3.5.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.jadegg2568"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt:0.9.1")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	// Lombok + MapStruct integration
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// Databases + Migrations
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	// Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Настройка MapStruct
tasks.withType<JavaCompile> {
	options.annotationProcessorPath = configurations.annotationProcessor.get()
	options.compilerArgs = listOf(
		"-parameters", // Spring Boot Gradle plugin doesn't add it automatically
		"-Amapstruct.defaultComponentModel=spring",
//		"-Amapstruct.unmappedTargetPolicy=IGNORE"
	)
}

tasks.withType<Test> {
	useJUnitPlatform()
}