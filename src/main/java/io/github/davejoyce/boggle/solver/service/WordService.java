/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver.service;

import io.github.davejoyce.boggle.solver.model.BoggleBoard;
import net.openhft.chronicle.map.ChronicleMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for finding dictionary words in a Boggle board.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
@Service
public class WordService {

  private final Logger logger;
  private final Map<Locale, ChronicleMap<Integer, String>> dictionaryRegistry;

  @Autowired
  public WordService(Map<Locale, ChronicleMap<Integer, String>> dictionaryRegistry) {
    this.dictionaryRegistry = dictionaryRegistry;
    this.logger = LoggerFactory.getLogger(getClass());
  }

  /**
   * Find the collection of distinct words in the specified Boggle board,
   * within the context of the given locale.
   *
   * @param boggleBoard Boggle board to be processed
   * @param locale Locale in which to search for words
   * @return set of distinct words found in the Boggle board, or empty set if
   *         no dictionary exists for the locale
   */
  public Set<String> findWords(BoggleBoard boggleBoard, Locale locale) {
    logger.info("Finding {} words in Boggle board: {}", locale.getDisplayLanguage(), boggleBoard);

    // 1. Convert selected letters back into a grid
    final String[][] letters = createLetterGrid(boggleBoard);

    // 2. Load the dictionary for the given locale
    ChronicleMap<Integer, String> dictionary = dictionaryRegistry.get(locale);
    if (null == dictionary) {
      logger.warn("No {} dictionary exists; found 0 words", locale.getDisplayLanguage());
      return Collections.emptySet();
    } else {
      logger.debug("Using {} dictionary...", locale.getDisplayLanguage());
    }

    // 3. Created boolean grid for keeping track of visited letters
    final boolean[][] visited = createdVisitGrid(boggleBoard);

    // 3. Walk the letter grid and find words from the dictionary
    final int rows = boggleBoard.getBoardSize().getValue(),
              cols = boggleBoard.getBoardSize().getValue();
    final SortedSet<String> foundWords = new TreeSet<>();
    String currentText = "";
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        findWordsFromStartingPosition(letters, visited, row, col, currentText, dictionary, foundWords);
      }
    }

    return foundWords;
  }

  /**
   * Convert the selected letters in the given Boggle board into a square grid
   * (2-dimensional array).
   *
   * @param boggleBoard Boggle board to be processed
   * @return grid of selected letters
   */
  String[][] createLetterGrid(BoggleBoard boggleBoard) {
    final int rows = boggleBoard.getBoardSize().getValue(),
              cols = boggleBoard.getBoardSize().getValue();
    final String[][] grid = new String[rows][cols];
    logger.debug("Creating grid of {} cells", (rows * cols));
    int idx = 0;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        grid[row][col] = boggleBoard.getLetters().get(idx);
        logger.debug("cell[{}][{}] = '{}'", row, col, boggleBoard.getLetters().get(idx));
        idx++;
      }
    }
    return grid;
  }

  /**
   * Create a grid of boolean flags for tracking which Boggle board letters
   * have been visited.
   *
   * @param boggleBoard Boggle board to be tracked
   * @return grid of boolean flags, initialized to false
   */
  boolean[][] createdVisitGrid(BoggleBoard boggleBoard) {
    final int rows = boggleBoard.getBoardSize().getValue(),
              cols = boggleBoard.getBoardSize().getValue();
    boolean[][] grid = new boolean[rows][cols];
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        grid[row][col] = false;
      }
    }
    return grid;
  }

  boolean isWord(String s, ChronicleMap<Integer, String> dictionary) {
    int hashCode = s.hashCode();
    return dictionary.containsKey(hashCode);
  }

  /**
   * Recursive method for traversing the specified letter grid in all possible
   * directions, checking a managed text string against the given dictionary
   * for valid words.
   *
   * @param letters letter grid to be traversed
   * @param visited boolean flag grid to be updated
   * @param currentRow current row position index
   * @param currentCol current column position index
   * @param currentText current text string to be updated / checked
   * @param dictionary dictionary to reference
   * @param foundWords set of found words
   */
  private void findWordsFromStartingPosition(String[][] letters,
                                             boolean[][] visited,
                                             int currentRow,
                                             int currentCol,
                                             String currentText,
                                             ChronicleMap<Integer, String> dictionary,
                                             SortedSet<String> foundWords) {
    // 1. Add this letter to current text, and mark it as visited
    //    NOTE: current text behaves as a stack
    currentText += letters[currentRow][currentCol];
    visited[currentRow][currentCol] = true;

    // 2. Search dictionary for current text, and add it to collection if found
    if (isWord(currentText, dictionary)) {
      foundWords.add(currentText);
      logger.debug("Found word: {}", currentText);
    }

    // 3. From current letter, travel 1 step in all possible directions,
    //    skipping visited letters
    //
    //    NOTES:
    //    A. Directions are limited when current letter is on an edge of the
    //       grid. Length of the letters 1st dimension works as edge, because
    //       the grid is square.
    //    B. Positions cannot be < 0; position 0,0 is top leftmost letter.
    for (int row = currentRow - 1; row <= currentRow + 1 && row < letters.length; row++) {
      for (int col = currentCol - 1; col <= currentCol + 1 && col < letters.length; col++) {
        if (0 <= row && 0 <= col && !visited[row][col]) {
          findWordsFromStartingPosition(letters, visited, row, col, currentText, dictionary, foundWords);
        }
      }
    }

    // 4. Pop this letter from current text, and unmark it as visited
    currentText = currentText.substring(0, (currentText.length() - 1));
    visited[currentRow][currentCol] = false;
  }

}
