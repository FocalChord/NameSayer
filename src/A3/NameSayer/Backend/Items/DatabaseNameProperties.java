package A3.NameSayer.Backend.Items;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DatabaseNameProperties {
    private StringProperty _name;
    private StringProperty _rating;
    private DatabaseName _dbName;

    public DatabaseNameProperties(DatabaseName dbName) {
        _dbName = dbName;
        _name = new SimpleStringProperty(dbName.getName());
        _rating = new SimpleStringProperty(dbName.getStringRating(true));
    }

    public DatabaseName getDBObject() {
        return _dbName;
    }

    public String getDBName() {
        return _name.get();
    }

    public String getDBRating() {
        return _rating.get();
    }

    public StringProperty dbNameProperty() {
        return _name;
    }

    public StringProperty dbRatingProperty() {
        return _rating;
    }


}
