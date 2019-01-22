package com.yunxi.lucky.lottery.core;

import com.yunxi.lucky.lottery.ServerMain;
import com.yunxi.lucky.lottery.data.Operation;
import com.yunxi.lucky.lottery.data.Store;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author 无双老师【云析学院】
 * @version 1.0.0 2019年12月23日
 * @since  1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServerMain.class)
public class TestLottery {

    /**
     张三:[
        {中奖等级:1,第几次:1},
        {中奖等级:1,第几次:2},
        {中奖等级:1,第几次:3}
     ]
     */
    private Map<String, List<Map<String, Object>>> data = new HashMap<>();

    @Autowired
    private Store store;

    @Autowired
    private LotteryService lotteryService;

    @Test
    public void init() {
        store.reset();
        Integer load = Operation.load(Integer.class, Operation.STEP_TAG);
        Assert.assertTrue(load.equals(1));
    }

    public void lottery(int num) {
        Integer maxStep = store.getMaxStep();
        IntStream.rangeClosed(1, maxStep).forEach(i -> {
            int level = store.getProcess().getLevel();
            Map<Long, String> lottery = lotteryService.lottery();
            lottery.values().forEach(name -> {
                List<Map<String, Object>> maps = data.get(name);
                if (null == maps) {
                    maps = new ArrayList<>();
                }
                Map<String, Object> m = new HashMap<>();
                m.put("level", level);
                m.put("seq", num);
                maps.add(m);
                data.put(name, maps);
            });
        });
    }

    @Test
    public void lottery100() {
        IntStream.rangeClosed(1, 1000).forEach(i -> {
            init();
            lottery(i);
        });
        data.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().size() - o1.getValue().size())
                .forEachOrdered(lucky -> {
                    String name = lucky.getKey();
                    System.out.print(name);
                    List<Map<String, Object>> luckyLogs = lucky.getValue();
                    System.out.print("(" + luckyLogs.size() + "):");
                    Map<Integer, Integer> counter = new HashMap<>();
                    for (Map<String, Object> log : luckyLogs) {
                        Integer level = (Integer) log.get("level");
                        Integer perLevel = counter.get(level);
                        if (null == perLevel) {
                            counter.put(level, 1);
                        } else {
                            counter.put(level, ++perLevel);
                        }
                    }
                    System.out.print(counter);
                    System.out.println();
                });

        // 检查重复
        data.keySet().forEach(name->{
            Map<String,Integer> check = new HashMap<>();
            data.get(name).forEach(map -> {
                Integer level = (Integer) map.get("level");
                Integer seq = (Integer)map.get("seq");
                Integer s = check.get(seq+name);
                if(null != s){
                    System.out.println("name:"+name+",level:"+level+",seq:"+seq);
                }
                else{
                    check.put(seq+name, level);
                }
            });
        });
    }

    @Test
    public void changeLuckyPerson(){
//        init();
//        lottery(1);
        store.replaced(15204767993l,15702783372l);
        store.replaced(13707585579l,15702807285l);
    }

    @Test
    public void specialLottery(){
        lotteryService.specialLottery("abc", 10);
        lotteryService.specialLottery("1235555", 10);
        lotteryService.specialLottery("哈哈哈", 10);
        lotteryService.specialLottery("$@&#*$&#*$", 10);
        lotteryService.specialLottery("……&*%……￥……&￥#", 10);
        lotteryService.specialLottery("       ", 10);

    }
}
