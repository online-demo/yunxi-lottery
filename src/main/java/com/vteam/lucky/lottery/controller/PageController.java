package com.vteam.lucky.lottery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月17日
 * @since soter 1.0.0
 */
@Controller
public class PageController {

    @RequestMapping("/index")
    public String index() {
        return "/index";
    }

    @RequestMapping("/setting")
    public String setting() {
        return "/setting";
    }
}
