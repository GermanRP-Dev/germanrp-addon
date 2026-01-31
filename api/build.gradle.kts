import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

dependencies {
    labyProcessor()
    labyApi("api")

    addonMavenDependency("com.github.GermanRP-Dev:germanrp-addon_labymod4-server-api-integration:1.1.2")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.INTERFACE
}