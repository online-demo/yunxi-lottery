package com.yunxi.lucky.lottery.core;

import com.yunxi.lucky.lottery.data.Store;
import com.yunxi.lucky.lottery.dto.Person;
import com.yunxi.lucky.lottery.dto.Process;
import com.yunxi.lucky.lottery.dto.Special;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 无双老师【云析学院】
 * @version 1.0.0 2019年12月23日
 * @since  1.0.0
 */
@Service
public class LotteryService {

    private static final Log log = LogFactory.getLog(LotteryService.class);

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
            // 权重小于0的即为不参与抽奖的
            return person.getWeight() > 0 && (level == -1 || level == process.getLevel());
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
        log.info("抽奖，当前步骤：" + process + ",中奖名单：" + luckyPersons);
        store.nextStep(luckyPersons);
        return ret;
    }

    /**
     * 特别奖项抽奖
     *
     * @param award
     * @param num
     * @return
     * @throws RuntimeException
     */
    public Map<Long, String> specialLottery(String award, int num) throws RuntimeException {
        Map<Long, Person> personMap = store.getUnselectedPerson();
        Map<Long, Integer> weightMap = new HashMap<>();
        // 设置当前抽奖者名单,权重相等
        personMap.values().forEach(person -> weightMap.put(person.getPhone(), 1));
        WeightRandom<Long, Integer> weight = new WeightRandom<>();
        Map<Long, String> ret = new HashMap<>();
        Set<Person> luckyPersons = new HashSet<>();
        weight.random(weightMap, num).forEach(phone -> {
            Person luckyPerson = personMap.get(phone);
            ret.put(phone, luckyPerson.getName());
            luckyPersons.add(luckyPerson);
        });
        log.info("特别奖项抽奖：" + award + ",中奖名单：" + luckyPersons);
        store.specialAward(award, luckyPersons);
        return ret;
    }

    /**
     * 替换中奖名单
     *
     * @param award
     * @param beforePhone
     * @return
     */
    public Map<Long, String> replaced(Object award, Long beforePhone) {
        Map<Long, Person> personMap = store.getUnselectedPerson();

        Map<Long, Integer> weightMap = new HashMap<>();
        if (award instanceof Integer) {
            int settingLevel = (int) award;
            // 设置当前抽奖者名单
            personMap.values().stream().filter(person -> {
                // 过滤设置了指定级别人员名单
                int level = person.getLevel();
                // 权重小于0的即为不参与抽奖的
                return person.getWeight() > 0 && (level == -1 || level == settingLevel);
            }).forEach(person -> weightMap.put(person.getPhone(),
                    // 设置了指定级别增权重的人员在其它级别使用最低权重
                    person.getLevel() == settingLevel ? person.getWeight() :
                            (person.getLevel() == -1 ? person.getWeight() : 1)
            ));
        } else {
            personMap.values().forEach(person -> weightMap.put(person.getPhone(), 1));
        }

        if (weightMap.isEmpty()) {
            throw new RuntimeException("找不到对应的奖项");
        }
        WeightRandom<Long, Integer> weight = new WeightRandom<>();
        Map<Long, String> ret = new HashMap<>();
        Set<Person> luckyPersons = new HashSet<>();
        weight.random(weightMap, 1).forEach(phone -> {
            Person luckyPerson = personMap.get(phone);
            ret.put(phone, luckyPerson.getName());
            luckyPersons.add(luckyPerson);
        });
        log.info("替换中奖者:" + beforePhone + "，当前奖项：" + award + ",中奖名单：" + luckyPersons);
        luckyPersons.forEach(person -> store.replaced(beforePhone, person.getPhone()));
        return ret;
    }

    public Map<Long, String> getLuckyPersonByLevel(Object award) {
        Map<Long, String> ret = new HashMap<>();
        store.getLuckyPerson(award).forEach(person -> {
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

    public Map<String, Map<Long, String>> getLuckyPerson() {
        Map<String, Map<Long, String>> ret = new HashMap<>();
        Map<String, Set<Person>> luckyPerson = store.getLuckyPerson();
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
        log.info("撤销上一步抽奖结果");
        store.preStep();
        return true;
    }

    public void createSpecial(String award, int num) {
        store.createSpecial(award, num);
    }

    public Special getSpecial() {
        return store.getSpecial();
    }
}
