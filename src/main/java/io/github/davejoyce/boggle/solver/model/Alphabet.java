/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver.model;

import java.io.Serializable;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * Alphabet for a particular locale.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
public class Alphabet implements Serializable {

  private static final long serialVersionUID = -4693996720276636935L;

  private final Locale locale;
  private final String[] letters;

  /**
   * Construct a new Alphabet for the specified locale, comprising the given
   * letters.
   *
   * @param locale locale in which this alphabet is used
   * @param letters letters of this alphabet
   */
  public Alphabet(Locale locale, String[] letters) {
    this.locale = requireNonNull(locale, "Locale argument cannot be null");
    this.letters = requireNonNull(letters, "Letter array argument cannot be null");
  }

  /**
   * @return locale for which this alphabet is used
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * @return letters of this alphabet
   */
  public String[] getLetters() {
    return letters;
  }

}
