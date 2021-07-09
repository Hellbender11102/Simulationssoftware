import controller.Logger;
import org.junit.*;

import java.io.*;

public class LoggerTest {
    Logger logger;

    @Before
    public void setUp() {
        logger = new Logger();
    }

    @After
    public void tearDown() {
        File outputFile = new File("out/Log.csv");
        File errFile = new File("out/Error.txt");
        int i = 0;
        while (outputFile.exists()) {
            outputFile.delete();
            outputFile = new File("out/Log" + i + ".csv");
            i++;
        }
        i = 0;
        while (errFile.exists()) {
            errFile.delete();
            errFile = new File("out/Error" + i + ".txt");
            i++;
        }
    }

    @Test
    public void TestLog() throws IOException {
        for (int i = 0; i < 10; i++) {
            logger.log("TestLog", i + "");
        }
        logger.saveLogToFile(false, 10);
        BufferedReader reader = new BufferedReader(new FileReader("out/Log.csv"));
        for (int i = 0; i < 11; i++) {
            if (i == 0)
                Assert.assertEquals("TestLog,", readLine(reader));
            if (i != 0)
                Assert.assertEquals(i - 1 + ",", readLine(reader));
        }
        reader.close();
    }

    @Test
    public void TestLogDouble() throws IOException {
        for (int i = 0; i < 10; i++) {
            logger.logDouble("TestLogDouble", 1.111111, 3);
        }
        for (int i = 0; i < 10; i++) {
            logger.logDouble("TestLogDouble", 1.111111, 5);
        }
        for (int i = 0; i < 10; i++) {
            logger.logDouble("TestLogDouble", 1.111111, 0);
        }
        for (int i = 0; i < 10; i++) {
            logger.logDouble("TestLogDouble", 1.111111, -1);
        }
        logger.saveFullLogToFile(false);
        BufferedReader reader = new BufferedReader(new FileReader("out/Log.csv"));
        for (int i = 0; i < 41; i++) {
            if (i == 0)
                Assert.assertEquals("TestLogDouble,", readLine(reader));
            else if (i < 11)
                Assert.assertEquals("1.111,", readLine(reader));
            else if (i < 21)
                Assert.assertEquals("1.11111,", readLine(reader));
            else if (i < 31)
                Assert.assertEquals("1.,", readLine(reader));
            else Assert.assertEquals("1.,", readLine(reader));
        }
        logger.saveLogToFile(false, 50);
        reader.close();
    }

    @Test
    public void TestSaveFullLogToFile() throws IOException {
        for (int i = 0; i < 3000; i++) {
            logger.log("TestSaveFullLogToFile", i + "");
        }
        logger.saveFullLogToFile(false);
        for (int i = 0; i < 3000; i++) {
            logger.log("TestSaveFullLogToFile", i + "");
        }
        logger.saveFullLogToFile(true);
        BufferedReader reader = new BufferedReader(new FileReader("out/Log.csv"));
        for (int i = 0; i < 6000; i++) {
            if (i == 0)
                Assert.assertEquals(readLine(reader), "TestSaveFullLogToFile,");
            else if(i <= 3000)
                Assert.assertEquals(readLine(reader), (i - 1)+",");
            else    Assert.assertEquals(readLine(reader), (i - 3001)+",");
        }
        reader.close();
    }

    @Test
    public void TestSaveFullLogToFileSmaller() throws IOException {
        for (int i = 0; i < 3000; i++) {
            logger.log("TestSaveFullLogToFileSmaller", i + "");
        }
        logger.saveFullLogToFile(true);
        for (int i = 0; i < 3000; i++) {
            logger.log("TestSaveFullLogToFileSmaller", i + "");
        }
        logger.saveFullLogToFile(false);
        BufferedReader reader = new BufferedReader(new FileReader("out/Log.csv"));
        for (int i = 0; i < 3000; i++) {
            if (i == 0)
                Assert.assertEquals(readLine(reader), "TestSaveFullLogToFileSmaller,");
            else
                Assert.assertEquals(readLine(reader), (i - 1)+",");
        }
        reader.close();
    }

    @Test
    public void TestDumpError() throws IOException {
        logger.dumpError("This is an test error.");
        logger.dumpError("It has more.");
        BufferedReader reader = new BufferedReader(new FileReader("out/Error.txt"));
        Assert.assertEquals(readLine(reader),"This is an test error.");
        Assert.assertEquals(readLine(reader),"It has more.");
        reader.close();
    }


    private String readLine(BufferedReader bufferedReader) {
        String line = "";
        try {
            if ((line = bufferedReader.readLine()) != null) {
                return line;
            }
        } catch (IOException f) {
            System.err.println(f.getMessage());
            Assert.fail();
        }
        return line;
    }
}
