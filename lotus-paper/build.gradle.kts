plugins {
    `java-library`
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":lotus-commons"))
    compileOnly(libs.paper.api)
    compileOnly(libs.jetbrains.annotations)
}

mavenPublishing {
    coordinates(group.toString(), "lotus-paper", version.toString())
    pom {
        name.set("Lotus Paper")
        description.set("Paper 1.21+ platform module for the Lotus GUI framework.")
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
    if (gradle.startParameter.taskNames.none { it.endsWith("publishToMavenLocal") }) {
        publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()
    }
}
