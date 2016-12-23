package com.vteam.lucky.lottery.core;

import com.vteam.lucky.lottery.data.Store;
import com.vteam.lucky.lottery.dto.Person;
import com.vteam.lucky.lottery.dto.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月23日
 * @since soter 1.0.0
 */
@Service
public class LotteryService {
    @Autowired
    private Store store;

    /**
     * 按数据存储中设定的步骤进行抽奖
     *
     * @return 手机号:姓名
     */
    public Map<Long, String> lottery() throws RuntimeException {
        Map<Long, Person> personMap = store.getUnselectedPerson();

        Map<Long, Integer> weightMap = new HashMap<>();

        Process process = store.getProcess();

        // 设置当前抽奖者名单
        personMap.values().stream().filter(person -> {
            // 过滤设置了指定级别人员名单
            int level = person.getLevel();
            return level == -1 || level == process.getLevel();
        }).forEach(person -> weightMap.put(person.getPhone(),
                // 设置了指定级别增权重的人员在其它级别使用最低权重
                person.getLevel() == process.getLevel() ? person.getWeight() :
                        (person.getLevel() == -1 ? person.getWeight() : 1)
        ));

        WeightRandom<Long, Integer> weight = new WeightRandom<>();
        Map<Long, String> ret = new HashMap<>();
        Set<Person> luckyPersons = new HashSet<>();
        weight.random(weightMap, process.getNum()).forEach(phone -> {
            Person luckyPerson = personMap.get(phone);
            ret.put(phone, luckyPerson.getName());
            luckyPersons.add(luckyPerson);
        });
        store.nextStep(luckyPersons);
        return ret;
    }

    public Map<Long, String> getLuckyPersonByLevel(Integer level) {
        Map<Long, String> ret = new HashMap<>();
        store.getLuckyPerson(level).forEach(person -> {
            ret.put(person.getPhone(), person.getName());
        });
        return ret;
    }

    public Map<Long, String> getPerson() {
        Map<Long, String> ret = new HashMap<>();
        store.getAllPerson().forEach(person -> {
            ret.put(person.getPhone(), person.getName());
        });
        return ret;
    }

    public Map<Integer, Map<Long, String>> getLuckyPerson() {
        Map<Integer, Map<Long, String>> ret = new HashMap<>();
        Map<Integer, Set<Person>> luckyPerson = store.getLuckyPerson();
        luckyPerson.keySet().forEach(level -> {
            Map<Long, String> personInfo = new HashMap<>();
            luckyPerson.get(level).forEach(person -> {
                personInfo.put(person.getPhone(), person.getName());
            });
            ret.put(level, personInfo);
        });
        return ret;
    }

    public Process getProcess() {
        return store.getProcess();
    }

    public boolean isDone() {
        return store.isDone();
    }

    public boolean unlottery() {
        store.preStep();
        return true;
    }
}
