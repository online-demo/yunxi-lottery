package com.yunxi.lucky.lottery.dto;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月29日
 * @since soter 1.0.0
 */
public class Special {
    private String award = "特别奖";
    private Integer num = 1;

    public Special(String award, Integer num) {
        this.award = award;
        this.num = num;
    }

    public Special() {
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
