/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver.controller;

import io.github.davejoyce.boggle.solver.model.BoardSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * Binds HTTP requests for Boggle Solver URLs to application logic.
 *
 * @author <a href="dave@osframwork.org">Dave Joyce</a>
 */
@Controller
public class BoggleSolverController {

  private final Logger logger;

  private String appMode;


  public BoggleSolverController(Environment environment) {
    this.appMode = environment.getProperty("app-mode");
    this.logger = LoggerFactory.getLogger(getClass());
  }

  @RequestMapping(value = "/")
  public String index(Model model) {
    model.addAttribute("today", new Date());
    model.addAttribute("mode", appMode);
    model.addAttribute("boardSizes", BoardSize.values());

    return "index";
  }

}
