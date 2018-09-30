/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */

package io.github.davejoyce.boggle.solver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.DEFAULT_LOCALE;
import static io.github.davejoyce.boggle.solver.ApplicationConstants.FORMAT_LOCALE_ALPHABET;

@Service
public class AlphabetService {

  private final Map<String, String[]> alphabets;
  private final String[] defaultAlphabet;
  private final Logger logger;

  public AlphabetService() {
    alphabets = new HashMap<>();
    logger = LoggerFactory.getLogger(getClass());
    defaultAlphabet = getAlphabet(DEFAULT_LOCALE);
  }

  public String[] getAlphabet(Locale locale) {
    String language = locale.getLanguage();
    try {
      return alphabets.computeIfAbsent(language, lang -> {
        String alphabetResourcePath = String.format(FORMAT_LOCALE_ALPHABET, lang);
        try {
          Path path = Paths.get(getClass().getClassLoader().getResource(alphabetResourcePath).toURI());
          StringBuilder buf = new StringBuilder();
          Stream<String> stream = Files.lines(path);
          stream.forEach(line -> buf.append(line).append(','));
          stream.close();
          // Get rid of trailing comma
          String trimmed = buf.substring(0, buf.length() - 1);
          logger.info("Loaded alphabet for language: {}", locale.getDisplayLanguage());
          return trimmed.split(",");
        } catch (Exception e) {
          throw new IllegalStateException("Failed to load alphabet for language: " + locale.getDisplayLanguage());
        }
      });
    } catch (IllegalStateException ise) {
      if (null != defaultAlphabet) {
        logger.warn("No alphabet located for language: {}; returning default", locale.getDisplayLanguage());
        return defaultAlphabet;
      } else {
        throw ise;
      }
    }
  }

}
