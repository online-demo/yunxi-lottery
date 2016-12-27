package com.vteam.lucky.lottery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

    @RequestMapping("/")
    public String welcome() {
        return "/index";
    }

    @RequestMapping("/main")
    public String lucky() {
        return "/main";
    }

    @RequestMapping("/setting")
    public String setting(@RequestParam(required = false)String pwd,Model model) {
        model.addAttribute("isManager",false);
        if(!StringUtils.isEmpty(pwd) && pwd.equals("vteam123")){
            model.addAttribute("isManager",true);
        }
        return "/setting";
    }
}
