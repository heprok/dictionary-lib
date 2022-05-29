import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version Versions.Spring.BOOT
    id("io.spring.dependency-management") version Versions.Spring.DEPENDENCY_MANAGEMENT
    id("com.diffplug.spotless") version Versions.Plugin.SPOTLESS

    kotlin("jvm") version Versions.KOTLIN
    kotlin("kapt") version Versions.KOTLIN
    kotlin("plugin.spring") version Versions.KOTLIN

    `java-library`
    `maven-publish`
}

group = Base.GROUP
version = Base.VERSION_NAME
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    mavenLocal()

    setOf(
        36319712, // BL Common
    ).forEach {
        maven {
            url = uri("https://gitlab.com/api/v4/projects/$it/packages/maven")
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
            credentials(HttpHeaderCredentials::class) {
                name = "Deploy-Token"
                value = System.getenv("CI_DEPLOY_PASSWORD")
            }
        }
    }
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.security:spring-security-core:${Versions.Spring.SECURITY}")
    implementation("org.springframework.security:spring-security-oauth2-jose:${Versions.Spring.SECURITY}")
    implementation("org.springframework.security:spring-security-oauth2-resource-server:${Versions.Spring.SECURITY}")
    implementation("org.aspectj:aspectjrt:1.7.3")
    kapt("org.springframework.boot:spring-boot-autoconfigure-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.briolink.lib:common:${Versions.Briolink.COMMON}")

    // FasterXML
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    kapt("jakarta.annotation:jakarta.annotation-api")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitLab"
            url = uri("https://gitlab.com/api/v4/projects/36548529/packages/maven")

            authentication {
                create<HttpHeaderAuthentication>("header")
            }

            credentials(HttpHeaderCredentials::class) {
                name = "Deploy-Token"
                value = System.getenv("GITLAB_DEPLOY_TOKEN")
            }
        }
        mavenLocal()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

spotless {
    kotlin {
        target("**/*.kt")

        // https://github.com/diffplug/spotless/issues/142
        ktlint(Versions.Plugin.KTLINT).userData(
            mapOf(
                "indent_style" to "space",
                "max_line_length" to "140",
                "indent_size" to "4",
                "ij_kotlin_code_style_defaults" to "KOTLIN_OFFICIAL",
                "ij_kotlin_line_comment_at_first_column" to "false",
                "ij_kotlin_line_comment_add_space" to "true",
                "ij_kotlin_name_count_to_use_star_import" to "2147483647",
                "ij_kotlin_name_count_to_use_star_import_for_members" to "2147483647",
                "ij_kotlin_keep_blank_lines_in_declarations" to "1",
                "ij_kotlin_keep_blank_lines_in_code" to "1",
                "ij_kotlin_keep_blank_lines_before_right_brace" to "0",
                "ij_kotlin_align_multiline_parameters" to "false",
                "ij_continuation_indent_size" to "4",
                "insert_final_newline" to "true",
            )
        )

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlinGradle {
        target("**/*.gradle.kts", "*.gradle.kts")

        ktlint().userData(mapOf("indent_size" to "4", "continuation_indent_size" to "4"))

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

tasks.withType<KotlinCompile> {
    dependsOn("spotlessApply")
    dependsOn("spotlessCheck")
}

tasks.compileJava {
    dependsOn(tasks.processResources)
}
