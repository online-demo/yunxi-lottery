package com.vteam.lucky.lottery.controller;

import com.vteam.lucky.lottery.data.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月17日
 * @since soter 1.0.0
 */
@Controller
public class SettingController {

    @Autowired
    private Store store;

    @ResponseBody
    @RequestMapping("/reset")
    public Boolean reset() throws RuntimeException{
        store.reset();
        return true;
    }
}
