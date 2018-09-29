/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.web.servlet.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.HEADER_ACCEPT_LANGUAGE;
import static io.github.davejoyce.boggle.solver.ApplicationConstants.LOCALES;

/**
 * Provides smart resolution of the application language to be loaded, based
 * upon examination of the browser-supplied 'Accept-Language' HTTP header.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
public class SmartLocaleResolver extends AcceptHeaderLocaleResolver {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    final String acceptLanguage = request.getHeader(HEADER_ACCEPT_LANGUAGE);
    Locale l = getDefaultLocale();
    if (!StringUtils.isEmpty(acceptLanguage)) {
      List<Locale.LanguageRange> list = Locale.LanguageRange.parse(acceptLanguage);
      l = Optional.ofNullable(Locale.lookup(list, LOCALES)).orElse(getDefaultLocale());
      logger.debug("Resolved locale: {}", l);
    }
    return l;
  }

}
