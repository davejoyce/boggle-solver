/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver;

import io.github.davejoyce.web.servlet.i18n.SmartLocaleResolver;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.*;

/**
 * Spring application configuration for Boggle Solver.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

  private final Logger logger = LoggerFactory.getLogger(getClass());

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

  @Bean
  public Map<Locale, ChronicleMap<Integer, String>> dictionaryRegistry(Environment environment) {
    final String dictionaryDir = environment.getProperty("java.io.tmpdir", "/tmp");
    logger.info("Loading dictionary data in directory: {}", dictionaryDir);

    // TODO Look for possible alternative to HashMap here
    Map<Locale, ChronicleMap<Integer, String>> dictionaryRegistry = new HashMap<>();

    Predicate<? super String> wordLengthFilter = line -> MAX_WORD_LENGTH >= line.length();

    // Iterate through application-supported locales, loading each dictionary
    for (Locale locale : LOCALES) {
      String dictionaryResourcePath = String.format(FORMAT_LOCALE_DICTIONARY, locale.getLanguage());
      try {
        // 1. Check if dictionary data file exists
        Path dictionaryMapFilePath = Paths.get(dictionaryDir, String.format("%s-dictionary-map.dat", locale.getLanguage()));
        File f = dictionaryMapFilePath.toFile();
        boolean newDictionary = false;
        if (!f.exists() && f.createNewFile()) {
          newDictionary = true;
          logger.debug("Created new dictionary data file: {}", f.getCanonicalPath());
        } else {
          logger.debug("Restoring dictionary data file: {}", f.getCanonicalPath());
        }

        Path path = Paths.get(getClass().getClassLoader().getResource(dictionaryResourcePath).toURI());
        // Lines in stream are UTF-8
        Stream<String> stream = Files.lines(path);

        ChronicleMap<Integer, String> dictionaryMap;
        // Filter out words longer than max acceptable length and calculate statistics
        // for ChronicleMap construction.
        IntSummaryStatistics stats = stream.filter(wordLengthFilter)
                                           .mapToInt(line -> line.getBytes(StandardCharsets.UTF_8).length)
                                           .summaryStatistics();
        stream.close();

        // Build dictionary ChronicleMap, using acquired statistics
        dictionaryMap = ChronicleMapBuilder.of(Integer.class, String.class)
                                           .name(locale.getLanguage() + "-dictionary-map")
                                           .constantKeySizeBySample(Integer.MAX_VALUE)
                                           .averageValueSize(stats.getAverage())
                                           .entries(stats.getCount())
                                           .createOrRecoverPersistedTo(f, true);
        logger.info("{} {} dictionary", (newDictionary ? "Created" : "Restored"), locale.getDisplayLanguage());

        stream = Files.lines(path).filter(wordLengthFilter);
        stream.forEach(word -> {
          final String w = word.trim().toLowerCase(locale);
          dictionaryMap.put(w.hashCode(), w);
        });
        stream.close();

        dictionaryRegistry.put(locale, dictionaryMap);
        logger.info("Registered {} dictionary (words: {})", locale.getDisplayLanguage(), dictionaryMap.size());
      } catch (Exception e) {
        throw new IllegalStateException("Failed to load dictionary for language: " + locale.getDisplayLanguage(), e);
      }
    }

    return dictionaryRegistry;
  }

}
