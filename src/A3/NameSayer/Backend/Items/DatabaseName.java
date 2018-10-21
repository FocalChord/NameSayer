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

    public DatabaseName(String name, String filePath) {
        _name = name;
        _filePath = filePath;
        _currentRating = Rating.NO_RATING;
    }

    public DatabaseName() {

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




}
