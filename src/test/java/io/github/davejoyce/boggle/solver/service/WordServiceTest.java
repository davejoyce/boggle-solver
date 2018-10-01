/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */

package io.github.davejoyce.boggle.solver.service;

import io.github.davejoyce.boggle.solver.model.BoardSize;
import io.github.davejoyce.boggle.solver.model.BoggleBoard;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.DEFAULT_LOCALE;
import static io.github.davejoyce.boggle.solver.ApplicationConstants.MAX_WORD_LENGTH;
import static org.testng.Assert.*;

public class WordServiceTest {

  @Test
  public void testFindWords() {
    // Create a 4x4 board with some words in it
    String[][] boardGrid = {
      { "w", "o", "z", "g" },
      { "r", "d", "qu", "v" },
      { "l", "e", "n", "t" },
      { "d", "f", "a", "n" }
    };

    // Flatten grid to list and create BoggleBoard object
    List<String> letters = new ArrayList<>();
    int idx = 0;
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++) {
        letters.add(idx, boardGrid[row][col]);
        idx++;
      }
    }
    BoggleBoard boggleBoard = new BoggleBoard(BoardSize.FOUR, letters);

    // Create small dictionary for default Locale
    ChronicleMap<Integer, String> dictionary = createDictionaryForDefaultLocale();
    Map<Locale, ChronicleMap<Integer, String>> registry = Collections.singletonMap(DEFAULT_LOCALE, dictionary);

    WordService service = new WordService(registry);

    Set<String> foundWords = service.findWords(boggleBoard, DEFAULT_LOCALE);
    assertNotNull(foundWords);
    assertFalse(foundWords.isEmpty());

    // In this case we know all dictionary words should have been found
    assertEquals(dictionary.size(), foundWords.size());
  }

  private ChronicleMap<Integer, String> createDictionaryForDefaultLocale() {
    String[] words = { "word", "world", "wore", "redo", "rode", "rent", "lent", "fan", "tan" };
    Predicate<? super String> wordLengthFilter = line -> 16 >= line.length();
    Stream<String> stream = Arrays.stream(words);
    IntSummaryStatistics stats = stream.filter(wordLengthFilter)
                                       .mapToInt(line -> line.getBytes(StandardCharsets.UTF_8).length)
                                       .summaryStatistics();
    stream.close();

    ChronicleMap<Integer, String> dictionary = ChronicleMapBuilder.of(Integer.class, String.class)
                                                                  .name(DEFAULT_LOCALE.getLanguage() + "-dictionary-map")
                                                                  .constantKeySizeBySample(Integer.MAX_VALUE)
                                                                  .averageValueSize(stats.getAverage())
                                                                  .entries(stats.getCount())
                                                                  .create();
    // Populate the dictionary
    stream = Arrays.stream(words);
    stream.forEach(word -> {
      dictionary.put(word.hashCode(), word);
    });

    return dictionary;
  }

}
