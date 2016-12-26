package com.vteam.lucky.lottery.controller;

import com.vteam.lucky.lottery.core.LotteryService;
import com.vteam.lucky.lottery.dto.Process;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月17日
 * @since soter 1.0.0
 */
@Controller
public class LotteryController {

    private static final Log log = LogFactory.getLog(LotteryController.class);

    @Autowired
    private LotteryService lotteryService;

    @ResponseBody
    @RequestMapping("/lottery")
    public Map<Long, String> lottery() {
        try {
            return lotteryService.lottery();
        } catch (RuntimeException e) {
        }
        return new HashMap<>();
    }

    @ResponseBody
    @RequestMapping("/unlottery")
    public Boolean unlottery() {
        return lotteryService.unlottery();
    }

    @ResponseBody
    @RequestMapping("/process")
    public Process getNowProcess() {
        return lotteryService.isDone() ? new Process() : lotteryService.getProcess();
    }

    @ResponseBody
    @RequestMapping("/person")
    public Map<Long, String> getAllPerson() {
        return lotteryService.getPerson();
    }

    @ResponseBody
    @RequestMapping("/lucky/{level}")
    public Map<Long, String> getLuckyPersonByLevel(
            @PathVariable(value = "level") Integer level) {
        return lotteryService.getLuckyPersonByLevel(level);
    }

    @ResponseBody
    @RequestMapping("/lucky")
    public Map<Integer, Map<Long, String>> getLuckyPerson() {
        return lotteryService.getLuckyPerson();
    }


}
