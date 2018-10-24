package A3.NameSayer.Backend.Items;

import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.RatingSystem.Rating;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseName implements Serializable {

    private String _name; // This field is for containing the name of the database name file
    private String _filePath; // This field provides the file path to the .wav file
    private Rating _previousRating; // This field is for the previous rating of the recording
    private Rating _currentRating; // This field is for the current rating of the recording
    private List<DatabaseName> _duplicateList; // This list contains all the names relating to the duplicate audio files
    private boolean _hasDuplicate; // This boolean checks if this name


    // Constructor for constructing the DatabaseName object
    public DatabaseName(String name, String filePath) {
        _name = name;
        _filePath = filePath;
        _currentRating = Rating.NO_RATING;
        _hasDuplicate = false;
    }

    // Adds the database name list to the object
    public void addDuplicateList(List<DatabaseName> dbNameList) {
        _duplicateList = dbNameList;
        _hasDuplicate = true;
    }

    // Typical getters and setters 
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

    public String getName() {
        return _name;
    }

    public String getPathToRecording() {

        if (_hasDuplicate) {
            for (DatabaseName db : _duplicateList) {
                if (db.getStringRating(true).equals(Database.GOOD_RATING)) {
                    return db._filePath;
                }
            }
        }


        return _filePath;
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

    public Rating getPreviousRating() {
        return _previousRating;
    }

    public String getRatingLine(boolean current) {
        String line = "Recording Name: " + _name +
                ", Quality of this recording is: ";

        line = (current) ? line + getStringRating(true) : line + getStringRating(false);

        return line;
    }




}
