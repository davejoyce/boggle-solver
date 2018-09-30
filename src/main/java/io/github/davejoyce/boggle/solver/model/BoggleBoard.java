/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.DEFAULT_BOARD_SIZE;

/**
 * Command object for representation of a submitted Boggle board to be solved.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
public class BoggleBoard {

  private BoardSize boardSize;
  private List<String> letters;

  public BoggleBoard(BoardSize boardSize, List<String> selectedLetters) {
    this.boardSize = boardSize;
    this.letters = new ArrayList<>(selectedLetters);
  }

  public BoggleBoard(BoardSize boardSize) {
    this(boardSize, Collections.emptyList());
  }

  public BoggleBoard() {
    this(DEFAULT_BOARD_SIZE, Collections.emptyList());
  }

  public BoardSize getBoardSize() {
    return boardSize;
  }

  public void setBoardSize(BoardSize boardSize) {
    if (null == boardSize) return;
    this.boardSize = boardSize;
  }

  public void setBoardSize(short boardSize) {
    setBoardSize(BoardSize.valueOf(boardSize));
  }

  public List<String> getLetters() {
    return letters;
  }

  public void setLetters(List<String> letters) {
    if (null == letters) return;
    this.letters = letters;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("BoggleBoard{");
    sb.append("boardSize=").append(boardSize)
      .append(", letters=").append(letters)
      .append('}');
    return sb.toString();
  }

}
