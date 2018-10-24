package A3.NameSayer.Frontend.Controllers;

import A3.NameSayer.Backend.Switch.SwitchScenes;
import A3.NameSayer.Backend.Switch.SwitchTo;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SoundCheckController {

    private Task _taskWorker;

    private boolean _stopCapture = false;

    private int _level;

    private byte[] _Buffer = new byte[2048];

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button backButton;

    @FXML
    private Button testButton;


    /**
     * Opens the targetline to allow the program to get audio input from the users mic. Reads this audio input and translates
     * it into a number which can be read as a level meter
     * @return
     */
    private Task createWorker(){
        return new Task(){
            @Override
            protected Object call() {

                try {
                    AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                    TargetDataLine targetLine = (TargetDataLine)AudioSystem.getLine(info);

                    targetLine.open();
                    targetLine.start();

                    //Reads and updates the level meter.
                    try {
                        while (!_stopCapture) {

                            if (targetLine.read(_Buffer, 0, _Buffer.length) > 0) {
                                _level = calculateRMSLevel(_Buffer);
                                updateProgress(_level,70);

                            }
                        }
                        targetLine.close();
                    } catch (Exception e) {

                    }


                } catch (LineUnavailableException e) {

                }

                return true;
            }
        };
    }

    /**
     * Reference: http://www.technogumbo.com/tutorials/Java-Microphone-Selection-And-Level-Monitoring/Java-Microphone-Selection-And-Level-Monitoring.php
     * Use  root mean squared method to calculate the amplitude of a section of wave data. I.E is the mic quiet or loud
     * @param audioData
     * @return
     */
    private int calculateRMSLevel(byte[] audioData){
        long lSum = 0;
        for(int i=0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for(int j=0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }

    /**
     * Method which controls the button clicks
     * @param e
     * @throws IOException
     */
    public void buttonClick(ActionEvent e) throws IOException {
        //When the user clicks the back button., stops the audio capturing and returns user to main menu
        if (e.getSource().equals(backButton)) {
            _stopCapture = true;
            SwitchScenes.getInstance().switchScene(SwitchTo.MAINMENU, e,SwitchScenes.largeWidth,SwitchScenes.largeHeight);


        }
        //When the user clicks the test button, starts the targetline
        if(e.getSource().equals(testButton)){
            testButton.setDisable(true);
            _taskWorker = createWorker();
            progressBar.setProgress(0.0);
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().bind(_taskWorker.progressProperty());

            Thread taskThread = new Thread(_taskWorker);
            taskThread.start();
        }
    }
}
