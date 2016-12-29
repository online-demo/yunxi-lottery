package com.vteam.lucky.lottery.controller;

import com.vteam.lucky.lottery.core.LotteryService;
import com.vteam.lucky.lottery.dto.Process;
import com.vteam.lucky.lottery.utils.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @RequestMapping("/specialLottery")
    public Map<Long, String> specialLottery(
            @RequestParam(value = "award", required = false) String award,
            @RequestParam(value = "num", defaultValue = "1", required = false) Integer num
    ) {
        try {
            return lotteryService.specialLottery(award, num);
        } catch (RuntimeException e) {
        }
        return new HashMap<>();
    }

    @ResponseBody
    @RequestMapping("/replaced")
    public Map<Long, String> replaced(
            @RequestParam(value = "award") String award,
            @RequestParam(value = "phone") Long phone
    ) {
        try {
            return lotteryService.replaced(Strings.isNumeric(award) ? Integer.valueOf(award) : award
                    , phone);
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
            @PathVariable(value = "level") String level) {
        return lotteryService.getLuckyPersonByLevel(Strings.isNumeric(level) ? Integer.valueOf(level) : level);
    }

    @ResponseBody
    @RequestMapping("/lucky")
    public Map<String, Map<Long, String>> getLuckyPerson() {
        return lotteryService.getLuckyPerson();
    }


}
