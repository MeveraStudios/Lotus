plugins {
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    group = "studio.mevera"
    version = "2.0.0"
}

subprojects {
    apply(plugin = "java-library")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
        withSourcesJar()
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    tasks.named<Delete>("clean") {
        delete("$projectDir/out", "$projectDir/bin")
    }
}
