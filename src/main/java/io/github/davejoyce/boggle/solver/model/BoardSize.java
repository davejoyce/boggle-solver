/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver.model;

/**
 * Typesafe enumeration of valid Boggle board sizes.
 *
 * @author <a href="dave@osframwork.org">Dave Joyce</a>
 */
public enum BoardSize {

  FOUR((short)4, "4x4"),
  FIVE((short)5, "5x5"),
  SIX((short)6, "6x6");

  private final short value;
  private final String label;

  BoardSize(short value, String label) {
    this.value = value;
    this.label = label;
  }

  public short getValue() {
    return value;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return getLabel();
  }

  public static BoardSize valueOf(short value) {
    BoardSize[] values = BoardSize.values();
    for (BoardSize boardSize : values) {
      if (value == boardSize.getValue()) return boardSize;
    }
    throw new IllegalArgumentException("Invalid BoardSize value: " + value);
  }

}
