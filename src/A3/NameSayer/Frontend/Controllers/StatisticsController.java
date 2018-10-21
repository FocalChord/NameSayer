package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchTo;
import A3.NameSayer.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class  StatisticsController implements Initializable {

    @FXML
    private Label timeID;

    private String text;

    private BufferedReader br = null;

    @FXML
    private Button backButton;

    public void buttonClick(ActionEvent e) throws IOException {
        if (e.getSource().equals(backButton)) {
            SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e, SwitchScenes.largeWidth, SwitchScenes.largeHeight);
            br.close();
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        String time = String.valueOf(duration / 60000000000L);
        timeID.setText(time);

    }
}
