package com.vteam.lucky.lottery.controller;

import com.vteam.lucky.lottery.core.LotteryService;
import com.vteam.lucky.lottery.data.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月17日
 * @since soter 1.0.0
 */
@Controller
public class SettingController {

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private Store store;

    @ResponseBody
    @RequestMapping("/reset")
    public Boolean reset() throws RuntimeException {
        store.reset();
        return true;
    }

    @ResponseBody
    @RequestMapping("/download")
    public void downloadLuckyPerson(HttpServletResponse response) {
        Map<String, Map<Long, String>> luckyPerson = lotteryService.getLuckyPerson();
        try {
            OutputStream os = response.getOutputStream();
            response.setContentType("application/text;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" +
                    new String("中奖者名单.txt".getBytes("gb2312"), "ISO8859-1"));
            StringBuilder item = new StringBuilder();
            item.append("奖项,姓名,电话\r\n");
            luckyPerson.keySet().forEach(level -> {
                String award = getCnLevel(level);
                luckyPerson.get(level).forEach((phone, name) -> {
                    item.append(award);
                    item.append(",");
                    item.append(name);
                    item.append(",");
                    item.append(phone);
                    item.append("\r\n");
                });
            });
            os.write(item.toString().getBytes(UTF_8));
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCnLevel(String level) {
        switch (level) {
            case "0":
                return "特等奖";
            case "1":
                return "一等奖";
            case "2":
                return "二等奖";
            case "3":
                return "三等奖";
            case "4":
                return "四等奖";
            default:
                return level;
        }
    }
}
