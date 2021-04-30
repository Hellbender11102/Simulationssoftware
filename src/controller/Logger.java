package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class Logger {
    private File outpotFile = new File("out/Log.csv");
    DecimalFormat df;
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

    synchronized
    public void logDouble(String key, double value, int decimalPlaces) {
        StringBuilder stringBuilder = new StringBuilder().append("#.");
        while (decimalPlaces-- > 0) {
            stringBuilder.append('#');
        }
        df = new DecimalFormat(stringBuilder.toString());
        log(key, df.format(value).replaceAll(",","."));
    }

    public void saveLogFile() throws IOException {
        FileWriter fileWriter = new FileWriter(outpotFile);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : logMap.keySet()) {
            stringBuilder.append(key).append(", ");
        }
        stringBuilder.append('\n');
        Optional<Integer> longestList = logMap.values().stream()
                .map(list -> Math.max(list.size(), 0))
                .reduce((currLargest, nextItem) -> currLargest > nextItem ? currLargest : nextItem);
        int longestListSize = longestList.orElse(0);
        int i = 0;
        while (i < longestListSize) {
            for (String key : logMap.keySet()) {
                if (i < logMap.get(key).size())
                    stringBuilder.append(logMap.get(key).get(i)).append(", ");
                else stringBuilder.append("-NONE-, ");
                if (i % logMap.keySet().size() == 0 && i != 0) stringBuilder.append('\n');
                i++;
            }
        }
        fileWriter.write(stringBuilder.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
