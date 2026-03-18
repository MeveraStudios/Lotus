plugins {
    java
    id("com.vanniktech.maven.publish") version "0.29.0" // Note: 0.33.0 is not a valid version, latest is 0.29.0
    id("com.gradleup.shadow") version "8.3.2"
}

group = "studio.mevera"
version = "1.7.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/central")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    compileOnly("org.jetbrains:annotations:21.0.1")

    compileOnly("net.kyori:adventure-platform-bukkit:4.3.4")
    compileOnly("net.kyori:adventure-text-minimessage:4.19.0")

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.jetbrains:annotations:21.0.1")
    testImplementation("net.kyori:adventure-platform-bukkit:4.3.4")
    testImplementation("net.kyori:adventure-text-minimessage:4.19.0")
    testCompileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
}

java {
    withSourcesJar()
    withJavadocJar()
}

mavenPublishing {
    coordinates(group.toString(), "lotus", version.toString())

    pom {
        name.set("Lotus")
        description.set("A modern customizable scoreboard library for spigot development.")
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
            url.set("https://github.com/MeveraStudios/Scofi/")
            connection.set("scm:git:git://github.com/MeveraStudios/Lotus.git")
            developerConnection.set("scm:git:ssh://git@github.com/MeveraStudios/Lotus.git")
        }
    }

    if (!gradle.startParameter.taskNames.any { it == "publishToMavenLocal" }) {
        publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()
    }
}


// Javadoc configuration for Java 17
tasks.withType<Javadoc> {
    if (JavaVersion.current().isJava9Compatible) {
        val options = options as StandardJavadocDocletOptions
        options.addBooleanOption("html5", true)
        options.addStringOption("Xdoclint:none", "-quiet")
    }

    // Set source compatibility for javadoc
    options.source = "8"

    // Exclude implementation packages from javadoc
    exclude("**/impl/**")
    exclude("**/internal/**")
}

// Clean task enhancement
tasks.named<Delete>("clean") {
    delete("$projectDir/out")
    delete("$projectDir/bin")
}
