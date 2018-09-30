/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */
package io.github.davejoyce.boggle.solver.controller;

import io.github.davejoyce.boggle.solver.model.Alphabet;
import io.github.davejoyce.boggle.solver.model.BoardSize;
import io.github.davejoyce.boggle.solver.model.BoggleBoard;
import io.github.davejoyce.boggle.solver.service.AlphabetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Locale;

import static io.github.davejoyce.boggle.solver.ApplicationConstants.DEFAULT_BOARD_SIZE;

/**
 * Binds HTTP requests for Boggle Solver URLs to application logic.
 *
 * @author <a href="dave@osframework.org">Dave Joyce</a>
 */
@Controller
public class BoggleSolverController {

  private final Logger logger;

  private final String appMode;
  private final AlphabetService alphabetService;

  @Autowired
  public BoggleSolverController(Environment environment,
                                AlphabetService alphabetService) {
    appMode = environment.getProperty("app-mode");
    this.alphabetService = alphabetService;
    logger = LoggerFactory.getLogger(getClass());
    logger.debug("Application mode: {}", appMode);
  }

  @GetMapping("/")
  public String index(WebRequest request,
                      Model model) {
    return indexWithBoardSize(request, DEFAULT_BOARD_SIZE.getValue(), model);
  }

  @GetMapping("/size/{boardSize}")
  public String indexWithBoardSize(WebRequest request,
                                   @PathVariable("boardSize") short size,
                                   Model model) {
    Locale locale = request.getLocale();
    Alphabet alphabet = alphabetService.getAlphabet(locale);

    // Box count = boardSize ^ 2
    BoardSize boardSize = BoardSize.valueOf(size);
    short boxCount = (short)(boardSize.getValue() * boardSize.getValue());

    BoggleBoard boggleBoard = new BoggleBoard(boardSize);

    logger.debug("Alphabet: (language: {}, letters: {})",
                 alphabet.getLocale().getDisplayLanguage(), alphabet.getLetters().length);
    logger.debug("Selected Boggle board size: {}", boardSize);

    // Populate model for render out to view
    model.addAttribute("today", new Date());
    model.addAttribute("mode", appMode);
    model.addAttribute("boardSizes", BoardSize.values());
    model.addAttribute("selectedSize", boardSize);
    model.addAttribute("boxCount", boxCount);
    model.addAttribute("alphabet", alphabetService.getAlphabet(locale));
    model.addAttribute("boggleBoard", boggleBoard);

    return "index";
  }

}
