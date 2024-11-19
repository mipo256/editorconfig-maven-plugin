package io.mpolivaha.maven.plugin.editorconfig.verifiers.impl;

import io.mpolivaha.maven.plugin.editorconfig.Editorconfig.Section;
import io.mpolivaha.maven.plugin.editorconfig.common.BufferedInputStream;
import io.mpolivaha.maven.plugin.editorconfig.common.ByteArrayLine;
import io.mpolivaha.maven.plugin.editorconfig.model.EndOfLine;
import io.mpolivaha.maven.plugin.editorconfig.model.Option;
import io.mpolivaha.maven.plugin.editorconfig.model.TrueFalse;
import io.mpolivaha.maven.plugin.editorconfig.verifiers.OptionValidationResult;
import io.mpolivaha.maven.plugin.editorconfig.verifiers.SpecOptionVerifier;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * {@link SpecOptionVerifier} for the insert_final_new_line option
 *
 * @author Mikhail Polivakha
 * @see TrueFalse
 */
public class InsertFinalNewLineOptionVerifier extends SpecOptionVerifier<TrueFalse>{

  protected InsertFinalNewLineOptionVerifier(Option targetOption) {
    super(targetOption);
  }

  @Override
  protected void forEachLine(ByteArrayLine line, int lineNumber, TrueFalse optionValue, OptionValidationResult result, Map<String, Object> executionContext) {
    if (line.isTheLastLine()) {
      // The very last line should always be the '\0' line, otherwise we're not terminating the last line with any end of line symbol
      if (line.lengthWithEoL() != 1) {
        result.addErrorMessage("Expected the end_of_line symbol to be present, but file terminates with EOF");
      }
    }
  }

  @Override
  public TrueFalse getValueFromSection(Section section) {
    return section.getInsertFinalNewLine();
  }
}
