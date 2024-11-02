package io.mpolivaha.maven.plugin.editorconfig.checkers;

import io.mpolivaha.maven.plugin.editorconfig.assertions.Assert;
import io.mpolivaha.maven.plugin.editorconfig.model.Option;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Result of validation of a given {@link Option} via {@link SpecOptionVerifier}
 *
 * @author Mikhail Polivakha
 */
public class OptionValidationResult {

  private final List<String> errorMessages;
  private final Path file;
  private final Option option;
  private final Object optionValue;

  public OptionValidationResult(Path file, Option option, Object optionValue) {
    this.file = file;
    this.option = option;
    this.optionValue = optionValue;
    this.errorMessages = new LinkedList<>();
  }

  public void addErrorMessage(String errorMessage) {
    errorMessages.add(errorMessage);
  }

  public boolean noErrors() {
    return errorMessages.isEmpty();
  }

  public String renderErrorMessage() {
    if (noErrors()) {
      Assert.fail("Called renderErrorMessage() on non-erroneous OptionViolations");
      return null; // unreachable code
    }
    var finalErrorMessage = new StringBuilder("For .editorconfig option %s=%s found %d violation on file : %s:\n".formatted(
        option.getKey(),
        optionValue,
        errorMessages.size(),
        file.getFileName()
    ));
    errorMessages.forEach(s -> finalErrorMessage.append("\t- %s\n".formatted(s)));
    return finalErrorMessage.toString();
  }
}
