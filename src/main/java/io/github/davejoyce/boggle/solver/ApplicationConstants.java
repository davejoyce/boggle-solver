/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Constants used throughout the Boggle Solver application.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
public class ApplicationConstants {

  /**
   * HTTP 'Accept-Language' header.
   */
  public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

  /**
   * List of recognized language locales as specified by the browser in the
   * 'Accept-Language' HTTP header.
   */
  public static final List<Locale> LOCALES = Arrays.asList(Locale.ENGLISH, Locale.FRENCH, new Locale("es"));

  /**
   * Default language locale to be applied, if none is specified.
   */
  public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

}
