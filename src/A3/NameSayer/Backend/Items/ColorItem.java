package A3.NameSayer.Backend.Items;

import javafx.scene.paint.Color;

public class ColorItem {

    private String text;
    private Color color;
    private final boolean inDatabase;

    public ColorItem(String text, Color color, boolean inDatabase) {
        this.text = text;
        this.color = color;
        this.inDatabase = inDatabase;
    }

    public String getText() {
        return text ;
    }

    public void setText(String text){
        this.text = text;
    }

    public boolean inDatabase(){
        return inDatabase;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor() {
        return color ;
    }
}

