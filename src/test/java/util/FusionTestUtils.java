package util;

import com.intellij.openapi.application.PathManager;

import java.io.File;

public class FusionTestUtils {
    /**
     * The root of the test data directory
     */
    public static final String BASE_TEST_DATA_PATH = findTestDataPath();

    private static String findTestDataPath() {
        File f = new File("testData");
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        throw new RuntimeException("Did not find test data path");
    }
}
