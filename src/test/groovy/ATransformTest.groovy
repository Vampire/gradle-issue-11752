import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static ATransform.MY_ATTRIBUTE

class ATransformTest extends Specification {
    def test() {
        given:
           def project = ProjectBuilder.builder().build()
           project.apply(plugin: JavaPlugin)
           ATransform.register(project.getDependencies())
           project.repositories.mavenCentral()
           project.dependencies {
               implementation('org.openjfx:javafx-base:20.0.2:win@jar')
               implementation('org.openjfx:javafx-controls:20.0.2:win@jar')
           }

        when:
           def filenames = project.configurations.runtimeClasspath {
               attributes {
                   attribute(MY_ATTRIBUTE, true)
               }
           }*.name

        then:
           filenames ==~ [
               'javafx-base-20.0.2-win-transformed.jar',
               'javafx-controls-20.0.2-win-transformed.jar'
           ]

        where:
           i << (1..1000)
    }
}
