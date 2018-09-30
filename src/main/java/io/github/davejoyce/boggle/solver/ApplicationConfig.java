/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver;

import io.github.davejoyce.web.servlet.i18n.SmartLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.DEFAULT_LOCALE;

/**
 * Spring application configuration for Boggle Solver.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

  @Bean
  public LocaleResolver localeResolver() {
    SmartLocaleResolver localeResolver = new SmartLocaleResolver();
    localeResolver.setDefaultLocale(DEFAULT_LOCALE);
    return localeResolver;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName("lang");
    return lci;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }

}
