package com.vteam.lucky.lottery.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vteam.lucky.lottery.dto.Person;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author li.cheng
 * @version 1.0.0 2016年12月22日
 * @since soter 1.0.0
 */
public class Operation {
    public static final String PROCESS_TAG = "/process";
    public static final String PERSON_TAG = "/person";

    public static final String LUCKY_TAG = "/lucky";
    public static final String STEP_TAG = "/step";

    private static String dataDir = "E:/VTeamLottery";

    public static void setDataDir(String str) {
        dataDir = str;
    }

    public static void save(Object obj, String tag) {
        try {
            Path path = Paths.get(dataDir + tag);
            if (!Files.exists(path)) {
                createFiles(path);
            }
            Files.write(path, JSONObject.toJSONBytes(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Set<Person>> loadLucky(Map<Integer, Set<Person>> dft) {
        Map<Integer, Set<Person>> ret = new HashMap<>();
        try {
            Path path = Paths.get(dataDir + LUCKY_TAG);
            if (Files.exists(path)) {

                HashMap<Integer, JSONArray> o = JSONObject.parseObject(Files.readAllBytes(path), HashMap.class);
                o.keySet().forEach(sort -> {
                    JSONArray jsonArray = o.get(sort);
                    Set<Person> persons = new HashSet<Person>();
                    jsonArray.forEach(map -> {
                        persons.add(map2person(map));
                    });
                    ret.put(sort, persons);
                });

            } else {
                createFiles(path);
                save(dft, LUCKY_TAG);
                return dft;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static Person map2person(Object obj) {
        Map<String, Object> map = (Map<String, Object>) obj;
        Person person = new Person();
        person.setName(map.get("name").toString());
        person.setPhone((long) map.get("phone"));
        person.setLevel((int) map.get("level"));
        person.setWeight((int) map.get("weight"));
        return person;
    }

    public static <T> T load(T dft, String tag) {
        try {
            Path path = Paths.get(dataDir + tag);
            if (Files.exists(path)) {
                return JSONObject.parseObject(Files.readAllBytes(
                        path
                ), dft.getClass());
            } else {
                createFiles(path);
                save(dft, tag);
                return dft;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T load(Class<T> clz, String tag) {
        try {
            Path path = Paths.get(dataDir + tag);
            if (Files.exists(path)) {
                return JSONObject.parseObject(Files.readAllBytes(
                        path
                ), clz);
            } else {
                createFiles(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createFiles(Path path) throws IOException {
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Files.createFile(path);
    }

}
