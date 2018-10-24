package A3.NameSayer.Backend.Items;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class exists as a wrapper class for the database name class
 *
 * It helps the database name class show its properties in the TableView in the database class
 */
public class DatabaseNameProperties {
    private StringProperty _name;
    private StringProperty _rating;

    public DatabaseNameProperties(DatabaseName dbName) {
        _name = new SimpleStringProperty(dbName.getName());
        _rating = new SimpleStringProperty(dbName.getStringRating(true));
    }


    // All the methods do what they say

    public String getDBName() {
        return _name.get();
    }

    public StringProperty dbNameProperty() {
        return _name;
    }

    public StringProperty dbRatingProperty() {
        return _rating;
    }


}
