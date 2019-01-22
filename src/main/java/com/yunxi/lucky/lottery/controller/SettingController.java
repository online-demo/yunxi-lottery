package com.yunxi.lucky.lottery.controller;

import com.yunxi.lucky.lottery.core.LotteryService;
import com.yunxi.lucky.lottery.data.Store;
import com.yunxi.lucky.lottery.server.Command;
import com.yunxi.lucky.lottery.server.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author 无双老师【云析学院】
 * @version 1.0.0 2019年12月17日
 * @since  1.0.0
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
    @RequestMapping("/control")
    public String mobileControl(String message){
        try {
            Helper.sendMessage(Command.valueOf(message));
            return Command.MSG.name();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    @ResponseBody
    @RequestMapping("/addReceived")
    public boolean addReceived(Long phone){
        store.addReceived(phone);
        return true;
    }

    @ResponseBody
    @RequestMapping("/remReceived")
    public boolean remReceived(Long phone){
        store.remReceived(phone);
        return true;
    }

    @ResponseBody
    @RequestMapping("/getReceived")
    public Set<Long> getReceived(){
        return store.getReceivedPhone();
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
