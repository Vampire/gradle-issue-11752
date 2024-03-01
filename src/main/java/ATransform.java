import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.transform.CacheableTransform;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.PathSensitive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE;
import static org.gradle.api.artifacts.type.ArtifactTypeDefinition.JAR_TYPE;
import static org.gradle.api.tasks.PathSensitivity.NAME_ONLY;

@CacheableTransform
public abstract class ATransform implements TransformAction<TransformParameters.None> {
    @PathSensitive(NAME_ONLY)
    @InputArtifact
    protected abstract Provider<FileSystemLocation> getInputArtifact();

    @Override
    public void transform(TransformOutputs outputs) {
        File inputFile = getInputArtifact().get().getAsFile();

        try (InputStream inStream = new FileInputStream(inputFile);
             OutputStream fileOut = new FileOutputStream(outputs.file(inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) + "-transformed.jar"));
             OutputStream outStream = new BufferedOutputStream(fileOut)) {
            byte[] buffer = new byte[1024];
            int i;
            while ((i = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Attribute<Boolean> MY_ATTRIBUTE = Attribute.of("my", Boolean.class);

    public static void register(DependencyHandler dependencies) {
        dependencies.getAttributesSchema().attribute(MY_ATTRIBUTE);
        dependencies.getArtifactTypes().findByName("jar").getAttributes().attribute(MY_ATTRIBUTE, false);
        dependencies.registerTransform(ATransform.class, it -> {
            it.getFrom().attribute(MY_ATTRIBUTE, false).attribute(ARTIFACT_TYPE_ATTRIBUTE, JAR_TYPE);
            it.getTo().attribute(MY_ATTRIBUTE, true).attribute(ARTIFACT_TYPE_ATTRIBUTE, JAR_TYPE);
        });
    }
}
