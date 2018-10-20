package A3.NameSayer.Backend.Items;

import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.RatingSystem.Rating;

import java.io.File;
import java.util.ArrayList;

public class DatabaseName {

    private String _name;
    private String _filePath;

    private Rating _previousRating;
    private Rating _currentRating;

    public DatabaseName(File f) {
        _name = extractName(f);
        _filePath = f.getAbsolutePath();
        _currentRating = Rating.NO_RATING;
    }

    public String getName() {
        return _name;
    }

    public String getPathToRecording() {
        return _filePath;
    }

    public ArrayList<String> getListOfRecording() {
        ArrayList<String> out = new ArrayList<>();
        out.add(_name);
        return out;
    }

    public String getStringRating(boolean current) {
        Rating whichRating = (current) ? _currentRating : _previousRating;

        switch (whichRating) {
            case GOOD:
                return Database.GOOD_RATING;
            case BAD:
                return Database.BAD_RATING;
            case NO_RATING:
                return Database.NO_RATING;
            default:
                return "";
        }
    }

    public Rating getCurrentRating() {
        return _currentRating;
    }

    public void setRating(Rating rating) {
        switch (rating) {
            case GOOD:
                _previousRating = _currentRating;
                _currentRating = Rating.GOOD;
                break;
            case BAD:
                _previousRating = _currentRating;
                _currentRating = Rating.BAD;
                break;
        }
    }

    public Rating getPreviousRating() {
        return _previousRating;
    }

    public String getRatingLine(boolean current) {
        String line = "Recording Name: " + _name +
                ", Quality of this recording is: ";

        line = (current) ? line + getStringRating(true) : line + getStringRating(false);

        return line;
    }

    private String extractName(File file) {
        String name = file.getName().substring(0, file.getName().length() - 4); // Remove .wav
        name = name.substring(findUnderscore(name)); // Extract just the name


        // Capitalize first letter

        if (!Character.isUpperCase(name.charAt(0))) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }


        return name;
    }

    private int findUnderscore(String s) {
        int count = 0;
        int index = 0;
        char[] charArr = s.toCharArray();

        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '_') {
                count++;
            }

            if (count == 3) {
                index = i;
                break;
            }
        }
        return index + 1;
    }


}
