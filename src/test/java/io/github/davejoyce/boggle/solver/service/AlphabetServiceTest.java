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
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Locale;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.DEFAULT_LOCALE;
import static org.testng.Assert.*;

/**
 * Unit tests of {@link AlphabetService}.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
public class AlphabetServiceTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private AlphabetService service;

  @BeforeTest
  public void createAlphabetService() {
    service = new AlphabetService();
  }

  @Test
  public void testGetAlphabetNullLocale() {
    Alphabet alphabet = service.getAlphabet(null);
    assertNotNull(alphabet, "Expected non-null default alphabet");
    logger.debug("Retrieved default alphabet");
  }

  @Test(dataProvider = "goodLocales")
  public void testGetAlphabet(Locale locale) {
    Alphabet alphabet = service.getAlphabet(locale);
    assertNotNull(alphabet, "Expected non-null alphabet for: " + locale);
    logger.debug("Retrieved alphabet for: {}", locale);
  }

  @Test
  public void testGetAlphabetBadLocale() {
    Locale badLocale = Locale.CHINESE;
    Alphabet alphabet = service.getAlphabet(badLocale);
    assertNotNull(alphabet, "Expected non-null default alphabet");
    logger.debug("Retrieved default alphabet for: {}", badLocale);
  }

  @DataProvider
  public Object[][] goodLocales() {
    return new Object[][] {
      new Object[] { DEFAULT_LOCALE },
      new Object[] { new Locale("es") },
      new Object[] { new Locale("fr") }
    };
  }

}
