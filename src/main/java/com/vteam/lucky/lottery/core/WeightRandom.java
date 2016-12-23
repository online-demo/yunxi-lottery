package com.vteam.lucky.lottery.core;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeightRandom<K, V extends Number> {
    private TreeMap<Double, K> weightMap = new TreeMap<>();

    private void setWeightMap(List<Pair<K, V>> list) {
        weightMap = new TreeMap<>();
        for (Pair<K, V> pair : list) {
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey();//统一转为double
            this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());//权重累加
        }
    }

    public K random(Map<K, V> map) {
        setWeightMap(map2pair(map));
        double randomWeight = this.weightMap.lastKey() * Math.random();
        SortedMap<Double, K> tailMap = this.weightMap.tailMap(randomWeight, false);
        return this.weightMap.get(tailMap.firstKey());
    }

    public Collection<K> random(Map<K, V> map, int num) {
        if (map.size() < num) {
            throw new RuntimeException("More values can't be found ");
        }
        List<K> selected = new ArrayList<>();
        IntStream.range(0, num).forEach(i -> {
            K val = random(map);
            selected.add(val);
            map.remove(val);
        });
        return selected;
    }


    private List<Pair<K, V>> map2pair(Map<K, V> map) {
        return map.keySet().stream().
                map(k -> new Pair<>(k, map.get(k))).
                collect(Collectors.toList());
    }
}