package com.yunxi.lucky.lottery.dto;

import java.io.Serializable;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月23日
 * @since soter 1.0.0
 */
public class Person implements Serializable {
    private String name;
    private Long phone;
    private int level = -1;
    private int weight = 1;

    public Person() {
    }

    public Person(String csv) throws Exception {
        String[] str = csv.split(",");
        this.name = str[0];
        this.phone = Long.valueOf(str[1]);
        if (str.length > 2) {
            this.level = Integer.valueOf(str[2]);
            this.weight = Integer.valueOf(str[3]);
        }

    }

    public Person(String name, Long phone, int level, int weight) {
        this.name = name;
        this.phone = phone;
        this.level = level;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return ((Person) obj).getPhone().equals(this.getPhone());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return name + "," + phone + "," + level + "," + weight;
    }
}
