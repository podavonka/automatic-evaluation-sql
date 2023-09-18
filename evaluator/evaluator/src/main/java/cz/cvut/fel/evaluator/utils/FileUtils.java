package cz.cvut.fel.evaluator.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Contains methods necessary for working with files.
 */
@Slf4j
public class FileUtils {

    /**
     * Gets information about the directory where the program is running.
     *
     * @return Path to the directory where the program is running.
     */
    public static String getProgramPath() {
        log.info("Method getProgramPath was called");
        String currentdir = System.getProperty("user.dir");
        currentdir = currentdir.replace( "\\", "/" );
        return currentdir;
    }

    /**
     * Reads text from a file located at the received path.
     *
     * @param path Path to the file relative to the directory 'resources'.
     * @param callingClass Class which called the method.
     * @return Text read from the file.
     */
    public static String readString(String path, Class<?> callingClass) {
        log.info("Method readString was called with the path " + path + " and the calling class name " + callingClass);
        String readString;

        InputStream inputStream = callingClass.getResourceAsStream(path);
        assert inputStream != null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        readString = reader.lines().collect(Collectors.joining(System.lineSeparator()));

        return readString;
    }

    /**
     * Reads text from a file located at the received path.
     *
     * @param path Path to the PDF file relative to the program root path.
     * @return Text read from the file.
     * @throws IOException When reading a file.
     */
    public static String readPDF(String path) throws IOException {
        log.info("Method loadPDF was called with the path " + path);

        File loadedFile = new File(path);
        PDDocument document = PDDocument.load(loadedFile);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();

        String text = pdfTextStripper.getText(document).toUpperCase();
        document.close();

        return text;
    }
}
