plugins {
    java
    `java-library`
    id("com.vanniktech.maven.publish") version "0.29.0"
    id("com.gradleup.shadow") version "8.3.2"
}

group = "studio.mevera"
version = "2.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("org.jetbrains:annotations:26.0.2")

    testCompileOnly("io.papermc.paper:paper-api:26.1.2")
    testCompileOnly("org.jetbrains:annotations:26.0.2")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 25
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-serial", "-parameters"))
}

tasks.withType<Javadoc>().configureEach {
    val opts = options as StandardJavadocDocletOptions
    opts.addBooleanOption("html5", true)
    opts.addStringOption("Xdoclint:none", "-quiet")
    opts.encoding = "UTF-8"
    opts.source = "25"
    exclude("**/internal/**")
}

tasks.named<Delete>("clean") {
    delete("$projectDir/out", "$projectDir/bin")
}

mavenPublishing {
    coordinates(group.toString(), "lotus", version.toString())
    pom {
        name.set("Lotus")
        description.set("A modern, type-safe Paper menu framework.")
        inceptionYear.set("2026")
        url.set("https://github.com/MeveraStudios/Lotus/")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://mit-license.org/")
            }
        }
        developers {
            developer {
                id.set("mqzn")
                name.set("Mqzn")
                url.set("https://github.com/Mqzn/")
            }
        }
        scm {
            url.set("https://github.com/MeveraStudios/Lotus/")
            connection.set("scm:git:git://github.com/MeveraStudios/Lotus.git")
            developerConnection.set("scm:git:ssh://git@github.com/MeveraStudios/Lotus.git")
        }
    }
    if (gradle.startParameter.taskNames.none { it == "publishToMavenLocal" }) {
        publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()
    }
}
