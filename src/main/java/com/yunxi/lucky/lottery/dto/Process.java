package com.yunxi.lucky.lottery.dto;

/**
 * @author 无双老师【云析学院】
 * @version 1.0.0 2019年12月23日
 * @since  1.0.0
 */
public class Process {
    private int sort;
    private int level;
    private int num;
    private String desc;

    public Process() {
    }

    public Process(String csv) throws Exception {
        String[] str = csv.split(",");
        this.sort = Integer.valueOf(str[0]);
        this.level = Integer.valueOf(str[1]);
        this.num = Integer.valueOf(str[2]);
        this.desc = str[3];
    }

    public Process(int sort, int level, int num,String desc) {
        this.sort = sort;
        this.level = level;
        this.num = num;
        this.desc = desc;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return sort+","+level+","+num+","+desc;
    }
}
