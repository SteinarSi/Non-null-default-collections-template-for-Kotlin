import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
	kotlin("jvm") version "1.9.25"
	id("org.openapi.generator") version "7.8.0"
}

group = "no.kantega"
version = "1.0.0"
description = "OpenAPI Generator template for kotlin-spring with non-null default values for optional collections"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.swagger.core.v3:swagger-annotations:2.2.28")
	implementation("jakarta.validation:jakarta.validation-api:3.1.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.register<GenerateTask>("generateExampleApi") {
	templateDir.set("$projectDir/src/main/resources/templates") // <==== This is important, don't forget

	generatorName.set("kotlin-spring")
	inputSpec.set("$rootDir/src/main/resources/openapi/example-api.json")
	outputDir.set("$buildDir/generated/example-api")
	modelPackage.set("no.kantega.generated.example")
	globalProperties.put("models", "")
	configOptions.put("useSpringBoot3", "true")

	sourceSets["main"].java.srcDir(file("$buildDir/generated/example-api/src/main"))
}

tasks.register<GenerateTask>("generateMutableExampleApi") {
	templateDir.set("$projectDir/src/main/resources/templates") // <==== This is important, don't forget

	generatorName.set("kotlin-spring")
	inputSpec.set("$rootDir/src/main/resources/openapi/example-api.json")
	outputDir.set("$buildDir/generated/mutable-example-api")
	modelPackage.set("no.kantega.generated.mutable")
	globalProperties.put("models", "")
	configOptions.put("useSpringBoot3", "true")
	configOptions.put("modelMutable", "true")

	sourceSets["main"].java.srcDir(file("$buildDir/generated/mutable-example-api/src/main"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.compileKotlin {
	dependsOn(tasks.getByName("generateExampleApi"))
	dependsOn(tasks.getByName("generateMutableExampleApi"))
}
