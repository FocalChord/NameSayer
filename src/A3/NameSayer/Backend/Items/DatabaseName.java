package A3.NameSayer.Backend.Items;

import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.RatingSystem.Rating;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseName implements Serializable {

    private String _name;
    private String _filePath;

    private Rating _previousRating;
    private Rating _currentRating;

    private List<DatabaseName> _duplicateList;
    private boolean _hasDuplicate;

    public DatabaseName(String name, String filePath) {
        _name = name;
        _filePath = filePath;
        _currentRating = Rating.NO_RATING;
        _hasDuplicate = false;
    }

    public void addDuplicateList(List<DatabaseName> dbNameList) {
        _duplicateList = dbNameList;
        _hasDuplicate = true;
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




}
