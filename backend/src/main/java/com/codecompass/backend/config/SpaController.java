package com.codecompass.backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping({"/", "/login", "/register", "/chat"})
    public String forward() {
        return "forward:/index.html";
    }
}