/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver.service;

import io.github.davejoyce.boggle.solver.model.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.DEFAULT_LOCALE;
import static io.github.davejoyce.boggle.solver.ApplicationConstants.FORMAT_LOCALE_ALPHABET;

/**
 * Service for provision of an alphabet for a particular {@link Locale}.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
@Service
public class AlphabetService {

  private final Map<Locale, Alphabet> alphabets;
  private final Alphabet defaultAlphabet;
  private final Logger logger;

  public AlphabetService() {
    alphabets = new HashMap<>();
    logger = LoggerFactory.getLogger(getClass());
    defaultAlphabet = getAlphabet(DEFAULT_LOCALE);
  }

  public Alphabet getAlphabet(Locale locale) {
    // Handle null parameter rationally
    if (null == locale) {
      return Optional.of(defaultAlphabet).orElseThrow(() -> new IllegalStateException("Missing default alphabet!"));
    }

    try {
      return alphabets.computeIfAbsent(locale, l -> {
        String alphabetResourcePath = String.format(FORMAT_LOCALE_ALPHABET, l.getLanguage());
        try {
          Path path = Paths.get(getClass().getClassLoader().getResource(alphabetResourcePath).toURI());
          StringBuilder buf = new StringBuilder();
          Stream<String> stream = Files.lines(path);
          stream.forEach(line -> buf.append(line).append(','));
          stream.close();
          // Get rid of trailing comma
          String trimmed = buf.substring(0, buf.length() - 1);
          logger.info("Loaded alphabet for language: {}", l.getDisplayLanguage());
          return new Alphabet(l, trimmed.split(","));
        } catch (Exception e) {
          throw new IllegalStateException("Failed to load alphabet for language: " + l.getDisplayLanguage());
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
