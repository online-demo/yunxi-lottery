package com.vteam.lucky.lottery.data;

import com.vteam.lucky.lottery.dto.Person;
import com.vteam.lucky.lottery.dto.Process;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.vteam.lucky.lottery.data.Operation.LUCKY_TAG;
import static com.vteam.lucky.lottery.data.Operation.STEP_TAG;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月23日
 * @since soter 1.0.0
 */
@Component
public class Store {

    private Map<Long, Person> person = new HashMap<>();

    private Map<Integer, Process> process = new HashMap<>();

    private Integer step = 1;

    private Map<Integer, Set<Person>> lucky = new HashMap<>();

    @Value("${vteam.lottery.data.path}")
    private String dataPath;

    public void init() {
        Operation.setDataDir(dataPath);

        try {
            List<String> personCsv = Files.readAllLines
                    (Paths.get(Store.class.getResource("/person.csv").toURI()));
            for (String line : personCsv) {
                Person p = new Person(line);
                person.put(p.getPhone(), p);
            }

            List<String> processCsv = Files.readAllLines
                    (Paths.get(Store.class.getResource("/process.csv").toURI()));
            for (String line : processCsv) {
                Process p = new Process(line);
                process.put(p.getSort(), p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        step = Operation.load(step, STEP_TAG);
        lucky = Operation.loadLucky(lucky);
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
        step = 1;
        lucky = new HashMap<>();
        Operation.save(lucky, LUCKY_TAG);
        Operation.save(step, STEP_TAG);
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
     * @param level
     * @return
     */
    public Collection<Person> getLuckyPerson(int level) {
        Collection<Person> persons = new ArrayList<>();
        getSort(level).forEach(sort -> {
            Set<Person> set = lucky.get(sort);
            if (null != set) {
                persons.addAll(set);
            }
        });
        return persons;
    }

    /**
     * 获取所有等级获奖人员名单
     *
     * @return
     */
    public Map<Integer, Set<Person>> getLuckyPerson() {
        Map<Integer, Set<Person>> luckyPersons = new HashMap<>();
        lucky.keySet().forEach(sort -> {
            int level = process.get(sort).getLevel();
            Set<Person> personSet = luckyPersons.get(level);
            if (null == personSet) {
                personSet = new HashSet<>();
            }
            personSet.addAll(lucky.get(sort));
            luckyPersons.put(level, personSet);
        });
        return luckyPersons;
    }

    /**
     * 获取未被抽中的人员名单
     *
     * @return
     */
    public Map<Long, Person> getUnselectedPerson() {
        Map<Long, Person> allPerson = new HashMap<>();
        allPerson.putAll(person);
        lucky.values().forEach(set->set.forEach(p->allPerson.remove(p.getPhone())));
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
        return lucky.size() == getStep();
    }


    private Collection<Integer> getSort(Integer level) {
        Collection<Integer> sorts = new ArrayList<>();
        process.keySet().stream().
                filter(sort -> process.get(sort).getLevel() == level).
                forEach(sorts::add);
        return sorts;
    }

}
