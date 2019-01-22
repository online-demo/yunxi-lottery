package com.yunxi.lucky.lottery.data;

import com.yunxi.lucky.lottery.dto.Person;
import com.yunxi.lucky.lottery.dto.Process;
import com.yunxi.lucky.lottery.dto.Special;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static com.yunxi.lucky.lottery.data.Operation.*;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月23日
 * @since soter 1.0.0
 */
@Component
public class Store {
    private static final Log log = LogFactory.getLog(Store.class);

    private Map<Long, Person> person = new HashMap<>();

    // {sort}:{person}
    private Map<Integer, Process> process = new HashMap<>();

    private Integer step = 1;

    // {sort}:[{person},{person}]
    private Map<Integer, Set<Person>> lucky = new HashMap<>();
    // 流程之外的特别中奖人员列表
    private Map<String, Set<Person>> specialLuck = new HashMap<>();

    private Set<Long> received = new HashSet<>();

    // 特别奖项设置队列
    private Queue<Special> queue = new LinkedBlockingQueue<>();

    public Store() {
        Operation.setDataDir(System.getProperty("user.home") + "/.lottery");

        try {
            Path personPath = Paths.get(Store.class.getResource("/person.csv").toURI());
            List<String> personCsv = Files.readAllLines(personPath);
            for (String line : personCsv) {
                Person p = new Person(line);
                person.put(p.getPhone(), p);
            }

            Path processPath = Paths.get(Store.class.getResource("/process.csv").toURI());
            List<String> processCsv = Files.readAllLines(processPath);
            int sort = 1;
            for (String line : processCsv) {
                Process p = new Process((sort++) + "," + line);
                process.put(p.getSort(), p);
            }

            // 检测到配置文件发生变化时，重置数据
            String savedPersonMD5 = Operation.load("", PERSON_TAG);
            String savedProcessMD5 = Operation.load("", PROCESS_TAG);
            String personMD5 = getFileMD5(personPath);
            String processMD5 = getFileMD5(processPath);
            if (StringUtils.isEmpty(savedPersonMD5)
                    || StringUtils.isEmpty(savedProcessMD5)
                    || !savedPersonMD5.equals(personMD5)
                    || !savedProcessMD5.equals(processMD5)) {
                log.info("检测到员工或流程配置发生变化,重置历史数据");
                Operation.save(personMD5, PERSON_TAG);
                Operation.save(processMD5, PROCESS_TAG);
                reset();
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        step = Operation.load(step, STEP_TAG);
        lucky = Operation.loadLucky(lucky);
        specialLuck = Operation.loadSpecialLuck(specialLuck);
        received = Operation.load(received,RECEIVED_TAG);
    }

    private String getFileMD5(Path path) {
        String strmd5 = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(Files.readAllBytes(path));
            BigInteger bi = new BigInteger(1, md5.digest());
            strmd5 = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strmd5;
    }


    public Collection<Person> getAllPerson() {
        return person.values();
    }

    public Collection<Process> getAllProcess() {
        return process.values();
    }

    public Integer getStep() {
        Integer maxStep = getMaxStep();
        return step > maxStep ? (step = maxStep) : step;
    }

    /**
     * 重新设置抽奖步骤
     */
    public void reset() {
        log.info("重置抽奖步骤");
        step = 1;
        lucky = new HashMap<>();
        specialLuck = new HashMap<>();
        received = new HashSet<>();
        queue.clear();
        Operation.save(lucky, LUCKY_TAG);
        Operation.save(specialLuck, SPECIAL_LUCKY_TAG);
        Operation.save(step, STEP_TAG);
        Operation.save(received,RECEIVED_TAG);

    }

    /**
     * 获取最终抽奖步骤
     *
     * @return
     */
    public Integer getMaxStep() {
        return process.keySet().stream().max((o1, o2) -> o1 - o2).get();
    }

    /**
     * 回退到上一步抽奖步骤
     */
    public void preStep() {
        Integer nowStep = getStep();
        // 已经完成抽奖时，只删除最后一步数据
        if (nowStep.equals(getMaxStep()) && null != lucky.get(nowStep)) {
            lucky.remove(nowStep);
        } else {
            step = nowStep > 1 ? --nowStep : 1;
            lucky.remove(step);
        }
        Operation.save(lucky, LUCKY_TAG);
        Operation.save(step, STEP_TAG);
    }

    /**
     * 保存中奖人员名单，并设置下一步骤
     *
     * @param persons
     */
    public void nextStep(Set<Person> persons) throws RuntimeException {
        if (step.equals(getMaxStep()) && null != lucky.get(step)) {
            throw new RuntimeException("抽奖流程已经完成");
        }
        lucky.put(step++, persons);
        Operation.save(lucky, LUCKY_TAG);
        Operation.save(step, STEP_TAG);
    }

    /**
     * 获取指定抽奖步骤获奖人员列表
     *
     * @param step
     * @return
     */
    public Collection<Person> getLuckyPersonBySort(int step) {
        return lucky.get(step);
    }

    /**
     * 获取指定等级获奖人员列表
     *
     * @param award
     * @return
     */
    public Collection<Person> getLuckyPerson(Object award) {
        Collection<Person> persons = new ArrayList<>();
        if (award instanceof Integer) {
            getSort((Integer) award).forEach(sort -> {
                Set<Person> set = lucky.get(sort);
                if (null != set) {
                    persons.addAll(set);
                }
            });
        } else {
            Set<Person> set = specialLuck.get(award.toString());
            if (null != set) {
                persons.addAll(set);
            }
        }

        return persons;
    }

    /**
     * 获取所有等级获奖人员名单
     *
     * @return
     */
    public Map<String, Set<Person>> getLuckyPerson() {
        Map<String, Set<Person>> luckyPersons = new HashMap<>();
        lucky.keySet().stream().filter(sort -> sort > 0).forEach(sort -> {
            int level = process.get(sort).getLevel();
            String award = level + "";
            Set<Person> personSet = luckyPersons.get(award);
            if (null == personSet) {
                personSet = new HashSet<>();
            }
            personSet.addAll(lucky.get(sort));
            luckyPersons.put(award, personSet);
        });
        specialLuck.keySet().stream().filter(award -> !award.startsWith("-")).forEach(award -> {
            Set<Person> personSet = luckyPersons.get(award);
            if (null == personSet) {
                personSet = new HashSet<>();
            }
            personSet.addAll(specialLuck.get(award));
            luckyPersons.put(award, personSet);
        });
        return luckyPersons;
    }

    /**
     * 替换中奖者
     *
     * @param beforePhone
     * @param afterPhone
     */
    public void replaced(Long beforePhone, Long afterPhone) {
        Integer replacedSort = null;
        Integer foundSort = null;
        Set<Person> replaced = null;
        Set<Person> base = null;
        boolean hasFound = false;
        Iterator<Integer> sorts = lucky.keySet().iterator();
        if(!lucky.isEmpty()){
            while (sorts.hasNext() && !hasFound){
                foundSort = sorts.next();
                base = lucky.get(foundSort);
                for (Person p : base) {
                    if (p.getPhone().equals(beforePhone)) {
                        base.remove(p);
                        replacedSort = -foundSort;
                        replaced = lucky.get(replacedSort);
                        if (null == replaced) {
                            replaced = new HashSet<>();
                        }
                        replaced.add(p);
                        hasFound = true;
                        base.add(person.get(afterPhone));
                        break;
                    }
                }

            }

            if(null != foundSort && null != replacedSort){
                lucky.put(replacedSort, replaced);
                lucky.put(foundSort, base);
                Operation.save(lucky, LUCKY_TAG);
            }
        }

        if(!specialLuck.isEmpty()){
            String replacedKey = "";
            String foundKey = "";
            replaced = null;
            base = null;
            hasFound = false;
            // 替换特别奖项中奖人员，在中奖名单中奖项名前面加“-”区分
            Iterator<String> awards = specialLuck.keySet().iterator();
            while (awards.hasNext() && !hasFound){
                foundKey = awards.next();
                base = specialLuck.get(foundKey);
                for (Person p : base) {
                    if (p.getPhone().equals(beforePhone)) {
                        base.remove(p);
                        replacedKey = "-" + foundKey;
                        replaced = specialLuck.get(replacedKey);
                        if (null == replaced) {
                            replaced = new HashSet<>();
                        }
                        replaced.add(p);
                        base.add(person.get(afterPhone));
                        hasFound = true;
                        break;
                    }
                }

            }

            if(null != replaced){
                specialLuck.put(replacedKey, replaced);
                specialLuck.put(foundKey, base);
                Operation.save(specialLuck, SPECIAL_LUCKY_TAG);
            }
        }





    }

    /**
     * 特别奖项数据保存
     *
     * @param award
     * @param luckPerson
     */
    public void specialAward(String award, Set<Person> luckPerson) {
        Set<Person> persons = specialLuck.get(award);
        if (null == persons) {
            persons = new HashSet<>();
        }
        persons.addAll(luckPerson);
        specialLuck.put(award, persons);
        Operation.save(specialLuck, SPECIAL_LUCKY_TAG);
    }


    /**
     * 获取未被抽中的人员名单
     *
     * @return
     */
    public Map<Long, Person> getUnselectedPerson() {
        Map<Long, Person> allPerson = new HashMap<>();
        allPerson.putAll(person);
        lucky.values().forEach(set -> set.forEach(p -> allPerson.remove(p.getPhone())));
        specialLuck.values().forEach(set -> set.forEach(p -> allPerson.remove(p.getPhone())));
        return allPerson;
    }

    /**
     * 获取当前抽奖步骤
     *
     * @return
     */
    public Process getProcess() {
        return process.get(getStep());
    }

    public boolean isDone() {
        return step > getMaxStep() || (step.equals(getMaxStep()) && null != lucky.get(step)) ;
    }

    public void createSpecial(String award, int num) {
        queue.add(new Special(award, num));
    }

    public Special getSpecial() {
        return queue.poll();
    }

    public Set<Long> getReceivedPhone(){
        return this.received;
    }

    public void addReceived(Long phone){
        this.received.add(phone);
        Operation.save(received,RECEIVED_TAG);
    }

    public void remReceived(Long phone){
        this.received.remove(phone);
        Operation.save(received,RECEIVED_TAG);
    }

    private Collection<Integer> getSort(Integer level) {
        Collection<Integer> sorts = new ArrayList<>();
        process.keySet().stream().
                filter(sort -> process.get(sort).getLevel() == level).
                forEach(sorts::add);
        return sorts;
    }

}
