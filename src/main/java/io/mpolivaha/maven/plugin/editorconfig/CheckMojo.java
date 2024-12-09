package io.mpolivaha.maven.plugin.editorconfig;

import io.mpolivaha.maven.plugin.editorconfig.Editorconfig.Section;
import io.mpolivaha.maven.plugin.editorconfig.assertions.Assert;
import io.mpolivaha.maven.plugin.editorconfig.verifiers.OptionsManager;
import io.mpolivaha.maven.plugin.editorconfig.config.PluginConfiguration;
import io.mpolivaha.maven.plugin.editorconfig.config.PluginConfiguration.Param;
import io.mpolivaha.maven.plugin.editorconfig.file.FileWalker;
import io.mpolivaha.maven.plugin.editorconfig.parser.EditorconfigParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "check", defaultPhase = LifecyclePhase.VALIDATE)
public class CheckMojo extends AbstractMojo {

  @Parameter(name = "strictMode", defaultValue = "true", required = true)
  private boolean strictMode;

  @Parameter(name = "editorconfig")
  private String editorConfigLocation;

  @Parameter(property = "project", readonly = true)
  private MavenProject project;

  public void execute() throws MojoExecutionException, MojoFailureException {
    PluginConfiguration.buildInstance(
        Map.of(
            Param.STRICT_MODE, strictMode,
            Param.LOG, getLog()
        )
    );

    if (editorConfigLocation != null && !editorConfigLocation.isEmpty()) {
      try {
        var rootEditorConfigIs = getEditorConfigInputStream();
        Editorconfig editorconfig = new EditorconfigParser().parse(rootEditorConfigIs);
        new FileWalker().walkRecursiveFilesInDirectory(
            editorconfig.getLocation(),
            (recursivelyFoundFile) -> {
              editorconfig
                  .findTargetSection(recursivelyFoundFile)
                  .ifPresent(section -> delegateToOptionsManager(recursivelyFoundFile, section));
            });
      } catch (IOException e) {
        Assert.sneakyThrows(e);
      }
    } else {
      // TODO: implement file tree search
    }
  }

  private Path getEditorConfigInputStream() throws MojoExecutionException {
    return new RootEditorConfigFileResolver()
        .findRootEditorConfig(project, editorConfigLocation)
        .orElseThrow(
            () -> new MojoExecutionException("The specified .editorconfig file was not found : '%s'".formatted(editorConfigLocation)));
  }

  private static void delegateToOptionsManager(Path file, Section section) {
    try {
      OptionsManager.getInstance().check(file, section);
    } catch (Throwable e) {
      Assert.sneakyThrows(e);
    }
  }
}
