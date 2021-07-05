package controller;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Logger {
    private File outputFile;
    private File errFile;
    DecimalFormat df;
    Thread saveThread;
    private boolean headWritten = false;
    private ConcurrentHashMap<String, List<String>> logMap = new ConcurrentHashMap<>();

    /**
     * Constructor
     * sets an output file for the current simulation
     */
    public Logger() {
        outputFile = new File("out/Log.csv");
        errFile = new File("out/Log.csv");
        int i = 0;
        while (outputFile.exists())
            outputFile = new File("out/Log" + i++ + ".csv");
        while (errFile.exists())
            errFile = new File("out/error" + i++ + ".csv");
    }

    /**
     * Inserts an entry to the logMap for given key
     *
     * @param key   String
     * @param value String
     */
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

    /**
     * Inserts an entry to the logMap for given key
     * cuts of the double value at the given decimal place
     *
     * @param key           String
     * @param value         String
     * @param decimalPlaces int
     */
    synchronized
    public void logDouble(String key, double value, int decimalPlaces) {
        StringBuilder stringBuilder = new StringBuilder().append("#.");
        while (decimalPlaces-- > 0) {
            stringBuilder.append('#');
        }
        df = new DecimalFormat(stringBuilder.toString());
        log(key, df.format(value).replaceAll(",", "."));
    }

    /**
     * Saves the current log to an file
     * If append is true inserts at the end of file
     * @param append boolean
     */
    synchronized
    public void saveFullLogToFile(boolean append) {
        Optional<Integer> longestList = logMap.values().stream()
                .map(List::size)
                .reduce((currLargest, nextItem) -> currLargest > nextItem ? currLargest : nextItem);
        int longestListSize = longestList.orElse(0);
        saveLogToFile(append, longestListSize);
    }

    /**
     * Writing in the logging file
     *
     * @param append          boolean
     * @param longestListSize int
     */
    synchronized
    public void saveLogToFile(boolean append, int longestListSize) {
        try {
            FileWriter fileWriter = new FileWriter(outputFile, append);
            StringBuilder stringBuilder = new StringBuilder();
            if (!headWritten) {
                for (String key : logMap.keySet()) {
                    stringBuilder.append(key).append(",");
                }
                headWritten = true;
                stringBuilder.append('\n');
            }
            int i = 0;
            while (i < longestListSize) {
                for (String key : logMap.keySet()) {
                    if (i < logMap.get(key).size())
                        stringBuilder.append(logMap.get(key).get(i)).append(",");
                    else stringBuilder.append(",");
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

    /**
     * Writes an error directly to the error File
     * @param errorMessage String
     */
    public void dumpError(String errorMessage) {
        try {
            System.err.println(errorMessage);
            FileWriter fileWriter = new FileWriter(errFile, true);
            fileWriter.write(errorMessage +"\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Fehler. Fehler konnte nicht geloggt werden!");
            e.printStackTrace();
        }
    }

    /**
     * Starts an thread if any key has more entries as 5000
     * The thread will save to an file and reduce the entries
     *
     * @param appendDataInFIle boolean
     */
    private void threadedSave(boolean appendDataInFIle) {
        synchronized (this) {
            if (logMap.values().stream().anyMatch(x -> x.size() > 5000) && (saveThread == null || !saveThread.isAlive())) {
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
