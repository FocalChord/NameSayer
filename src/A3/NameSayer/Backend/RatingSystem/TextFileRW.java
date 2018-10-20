package A3.NameSayer.Backend.RatingSystem;

import A3.NameSayer.Backend.Items.DatabaseName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// This class is to essentially read/write text files for the rating

// "Serializing Ratings"

public class TextFileRW {

    public static final String BAD_TXT_FILE_NAME = "Bad Quality Recordings.txt";
    public static final String GOOD_TXT_FILE_NAME = "Good Quality Recordings.txt";
    private static TextFileRW _textFileRW;
    private String text;
    private List<String> _badFileLines = new ArrayList<>();
    private List<String> _goodFileLines = new ArrayList<>();


    // Initializing and reading from he database
    private TextFileRW() throws IOException {
        loadData();
    }

    // Putting the singleton in "singleton"
    public static TextFileRW getInstance() {

        if (_textFileRW == null) {
            try {
                _textFileRW = new TextFileRW();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return _textFileRW;
    }

    public void changeRating(DatabaseName dbName, Rating rating) {

        dbName.setRating(rating);

        try {
            if (dbName.getPreviousRating() == Rating.NO_RATING) {
                addNewRating(dbName.getRatingLine(true), rating);
            } else {
                updateExistingRating(dbName.getRatingLine(false), dbName.getRatingLine(true), rating);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // Loading in the data from txt files
    private void loadData() throws IOException {
        File goodTxtFile = new File(GOOD_TXT_FILE_NAME);
        File badTxtFile = new File(BAD_TXT_FILE_NAME);

        if (goodTxtFile.exists()) {
            _goodFileLines = Files.readAllLines(goodTxtFile.toPath(), Charset.forName("UTF-8"));
        }

        if (badTxtFile.exists()) {
            _badFileLines = Files.readAllLines(badTxtFile.toPath(), Charset.forName("UTF-8"));

        }
    }


    // If the file already has a rating then we need to switch it if its changed.
    // Update good text file and bad text file.

    public void updateExistingRating(String line, String line1, Rating rating) throws IOException {
        switch (rating) {
            case GOOD:
                _badFileLines.removeIf(s -> s.equals(line));
                _goodFileLines.add(line1);
                makeFile(Rating.GOOD);
                renameAndDeleteFile(Rating.GOOD);
                makeFile(Rating.BAD);
                renameAndDeleteFile(Rating.BAD);
                break;
            case BAD:
                _goodFileLines.removeIf(s -> s.equals(line));
                _badFileLines.add(line1);
                makeFile(Rating.GOOD);
                renameAndDeleteFile(Rating.GOOD);
                makeFile(Rating.BAD);
                renameAndDeleteFile(Rating.BAD);
                break;
        }
    }

    // This is the first rating for a file
    // So just add a line to either file

    public void addNewRating(String line, Rating rating) throws IOException {
        switch (rating) {
            case GOOD:
                _goodFileLines.add(line);
                break;
            case BAD:
                _badFileLines.add(line);
                break;
            default:
                break;
        }
        makeFile(rating);
        renameAndDeleteFile(rating);
    }

    // TO ensure the user doesn't mess around with the files after they close the name sayer module
    // Make the files read only

    public void onAppClose() {
        File goodTxtFile = new File(TextFileRW.GOOD_TXT_FILE_NAME);
        File badTxtFile = new File(TextFileRW.BAD_TXT_FILE_NAME);

        if (goodTxtFile.exists()) {
            goodTxtFile.setReadOnly();
        }

        if (badTxtFile.exists()) {
            badTxtFile.setReadOnly();
        }
    }

    /*
        Create a temporary file first before overwrriting
     */

    private void makeFile(Rating rating) throws IOException {
        Path path1 = Paths.get("tmp.txt");
        switch (rating) {
            case GOOD:
                Files.write(path1, _goodFileLines, Charset.forName("UTF-8"));
                break;
            case BAD:
                Files.write(path1, _badFileLines, Charset.forName("UTF-8"));
                break;
            default:
                break;
        }

    }

    private void renameAndDeleteFile(Rating rating) {
        File file1 = new File("tmp.txt");
        File file2 = new File("");
        String newName = "";
        switch (rating) {
            case GOOD:
                file2 = new File(GOOD_TXT_FILE_NAME);
                newName = GOOD_TXT_FILE_NAME;
                break;
            case BAD:
                file2 = new File(BAD_TXT_FILE_NAME);
                newName = BAD_TXT_FILE_NAME;
                break;
            default:
                break;
        }


        if (file2.exists()) {
            file2.delete();
        }

        file1.renameTo(new File(newName));
    }

    /*public void saveTime() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("time.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        text = null;
        try {
            text = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Long textTime = Long.valueOf(text);
        textTime = textTime * 60000000000L;
        long duration = System.nanoTime() + textTime - Main.startTime;
        String time = String.valueOf(duration / 6000000000L);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("time.txt"), "utf-8"))) {
            writer.write(time);
        } catch (IOException e) {

        }
    }*/


}
