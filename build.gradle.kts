plugins {
    java
    id("com.vanniktech.maven.publish") version "0.29.0"
    id("com.gradleup.shadow") version "8.3.2"
}

group = "studio.mevera"
version = "2.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    //withJavadocJar()
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.0.2")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}


tasks.named<Delete>("clean") {
    delete("$projectDir/out", "$projectDir/bin")
}

mavenPublishing {
    coordinates(group.toString(), "lotus", version.toString())
    pom {
        name.set("Lotus")
        description.set("A modern, type-safe Paper GUI framework.")
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
