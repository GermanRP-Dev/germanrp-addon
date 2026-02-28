import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

dependencies {
    labyProcessor()
    labyApi("api")

    addonMavenDependency(libs.server.api.integration)

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.INTERFACE
}