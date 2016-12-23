package com.vteam.lucky.lottery.core;

import com.vteam.lucky.lottery.ServerMain;
import com.vteam.lucky.lottery.data.Operation;
import com.vteam.lucky.lottery.data.Store;
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
 * @author li.cheng
 * @version 1.0.0 2016年12月23日
 * @since soter 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServerMain.class)
public class TestLottery {

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
        Map<Long, String> max = lotteryService.getLuckyPersonByLevel(0);
        System.out.println("TestLottery.lottery" + num);
        Assert.assertEquals("范云", max.get(13506918060l));
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
    }
}
