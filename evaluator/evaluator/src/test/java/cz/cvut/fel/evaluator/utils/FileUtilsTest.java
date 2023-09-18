package cz.cvut.fel.evaluator.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class FileUtilsTest {

    @Test
    void getProgramPath_ReturnsAbsoluteProgramPath() {
        String expectedResult = System.getProperty("user.dir").replace( "\\", "/" );
        String actualResult = FileUtils.getProgramPath();

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void readString_ValidFile_Success() {
        String expectedResult = "FILE CONTENT";
        String actualResult = FileUtils.readString("/assertions/actual-read-string.txt", this.getClass());

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void readString_InvalidFilePath_CallingClassNameIsReturned() {
        String expectedResult = "FileUtilsTest.class";
        String actualResult = FileUtils.readString("", this.getClass());

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void readPDF_ValidFile_Success() throws IOException {
        String expectedResult = "PDF FILE CONTENT \n";
        String actualResult = FileUtils
                .readPDF("src/test/resources/assertions/actual-read-pdf.pdf")
                .replaceAll("(\\r)?\\n|\\r", "\n");

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void readPDF_InvalidFilePath_IOExceptionIsThrown() {
        Assertions.assertThrows(IOException.class, () -> FileUtils.readPDF(""));
    }
}
