package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Logger {
    private File outpotFile = new File("out/Log.csv");

    private HashMap<String, List<String>> logMap = new HashMap<String, List<String>>();

    synchronized
    public void log(String key, String value) {
        if (logMap.containsKey(key)) {
            logMap.get(key).add(value);
        } else {
            List<String> list = new LinkedList<>();
            list.add(value);
            logMap.put(key, list);
        }

    }

    public void saveLogFile() throws IOException {
        FileWriter fileWriter = new FileWriter(outpotFile);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : logMap.keySet()) {
            stringBuilder.append(key);
            stringBuilder.append(" , ");
        }
        stringBuilder.append('\n');
        Optional<Integer> longestList = logMap.values().stream()
                .map(list -> Math.max(list.size(),0))
                .reduce((currLargest, nextItem) -> currLargest > nextItem ? currLargest : nextItem);
        int longestListSize = longestList.orElse(0);
        int i = 0;
        while (i < longestListSize) {
            for (String key : logMap.keySet()) {
                if (logMap.get(key).size() > i)
                    stringBuilder.append(logMap.get(key).get(i) + " , ");
                else stringBuilder.append("-NONE- , ");
                if (i++ % logMap.keySet().size() == 0 && i != 0) stringBuilder.append('\n');
            }
        }
        fileWriter.write(stringBuilder.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
