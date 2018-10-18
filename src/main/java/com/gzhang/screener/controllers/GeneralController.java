package com.gzhang.screener.controllers;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;


/**
 * used for mapping '/' to swagger-ui
 */
@Controller
@ApiIgnore
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
public class GeneralController {

    @GetMapping("/")
    public String getRoot() {
        return "redirect:/swagger-ui.html";
    }
}
