package io.github.davejoyce.boggle.solver.controller;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class BoggleSolverController {

  private String appMode;


  public BoggleSolverController(Environment environment) {
    this.appMode = environment.getProperty("app-mode");
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index(Model model) {
    model.addAttribute("timestamp", new Date());
    model.addAttribute("mode", appMode);

    return "index";
  }

}
