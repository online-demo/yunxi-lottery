package com.vteam.lucky.lottery.controller;

import com.vteam.lucky.lottery.core.LotteryService;
import com.vteam.lucky.lottery.dto.Special;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LotteryService lotteryService;

    @RequestMapping("/index")
    public String index() {
        return "/index";
    }

    @RequestMapping("/")
    public String welcome() {
        return "/index";
    }

    @RequestMapping("/main")
    public String main(Model model) {
        Special special = lotteryService.getSpecial();
        if(null != special){
            model.addAttribute("award",special.getAward());
            model.addAttribute("num",special.getNum());
        }
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

    @RequestMapping("/award")
    public String award(
            @RequestParam(value = "type",defaultValue = "1",required = false) Integer type,
            @RequestParam(value = "award") String award,
            @RequestParam(value = "num",defaultValue = "1",required = false) Integer num,
            @RequestParam(value = "phone",required = false) Long phone,
            @RequestParam(value = "name",required = false,defaultValue = "") String name,
            Model model) {
        model.addAttribute("type",type);
        model.addAttribute("award",award);
        model.addAttribute("num",num);
        model.addAttribute("phone",phone);
        model.addAttribute("name",name);
        return "/award";
    }


}
