package com.vteam.lucky.lottery.controller;

import com.vteam.lucky.lottery.core.LotteryService;
import com.vteam.lucky.lottery.dto.Special;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LotteryService lotteryService;

    @RequestMapping("/index")
    public String index() {
        return "/index";
    }

    @RequestMapping("/m")
    public String show() {
        return "/control";
    }

    @RequestMapping("/")
    public String welcome() {
        return "/index";
    }

    @RequestMapping("/main")
    public String main(Model model) {
        Special special = lotteryService.getSpecial();
        if (null != special) {
            model.addAttribute("award", special.getAward());
            model.addAttribute("num", special.getNum());
        }
        return "/main";
    }

    @RequestMapping("/c")
    public String setting(Model model) {
        model.addAttribute("isManager", false);
        return "/console";
    }

    @RequestMapping("/a")
    public String admin(Model model) {
        model.addAttribute("isManager", true);
        return "/console";
    }

    @RequestMapping("/l")
    public String received() {
        return "/receive";
    }

    @RequestMapping("/award")
    public String award(
            @RequestParam(value = "type", defaultValue = "1", required = false) Integer type,
            @RequestParam(value = "award") String award,
            @RequestParam(value = "num", defaultValue = "1", required = false) Integer num,
            @RequestParam(value = "phone", required = false) Long phone,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            Model model) {
        model.addAttribute("type", type);
        model.addAttribute("award", award);
        model.addAttribute("num", num);
        model.addAttribute("phone", phone);
        model.addAttribute("name", name);
        return "/award";
    }


}
