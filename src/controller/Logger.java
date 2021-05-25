package controller;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {
    private File outpotFile = new File("out/Log.csv");
    DecimalFormat df;
    Thread saveThread;
    private ConcurrentHashMap<String, List<String>> logMap = new ConcurrentHashMap<>();

    synchronized
    public void log(String key, String value) {
            threadedSave(true);
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
        log(key, df.format(value).replaceAll(",", "."));
    }

    synchronized
    public void saveFullLogToFile(boolean append) {
        Optional<Integer> longestList = logMap.values().stream()
                .map(List::size)
                .reduce((currLargest, nextItem) -> currLargest > nextItem ? currLargest : nextItem);
        int longestListSize = longestList.orElse(0);
        saveLogToFile(append, longestListSize);
    }

    synchronized
    public void saveLogToFile(boolean append, int longestListSize) {
        try {
            FileWriter fileWriter = new FileWriter(outpotFile, append);
            StringBuilder stringBuilder = new StringBuilder();
            for (String key : logMap.keySet()) {
                stringBuilder.append(key).append(", ");
            }
            stringBuilder.append('\n');
            int i = 0;
            while (i < longestListSize) {
                for (String key : logMap.keySet()) {
                    if (i < logMap.get(key).size())
                        stringBuilder.append(logMap.get(key).get(i)).append(", ");
                    else stringBuilder.append(", ");
                }
               stringBuilder.append("\n");
                i++;
            }
            logMap.values().clear();
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void threadedSave(boolean appendDataInFIle) {
        synchronized (this) {
            if (logMap.values().stream().anyMatch(x -> x.size() > 2000) && (saveThread == null || !saveThread.isAlive())) {
                saveThread = (new Thread(() -> {
                    Optional<Integer> optionalListSize;
                    optionalListSize = logMap.values().stream().map(List::size)
                            .reduce((currentShortest, nextItem) -> currentShortest < nextItem ? currentShortest : nextItem);
                    int listSize = optionalListSize.orElse(0);
                    saveLogToFile(appendDataInFIle, listSize);
                    for (List<String> list : logMap.values()) {
                        if (list.size() > listSize && listSize > 0) {
                            list.subList(listSize - 1, list.size() - 1);
                        }
                    }
                }));
                saveThread.start();
            }
        }
    }
}
