/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoggleSolverApplication {

  public static void main(String[] args) {
    SpringApplication.run(new Class<?>[]{ BoggleSolverApplication.class, ApplicationConfig.class }, args);
  }

}
