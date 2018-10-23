package A3.NameSayer.Backend.Audio;

import A3.NameSayer.Backend.Databases.Database;
import A3.NameSayer.Backend.Items.DatabaseName;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class AudioPlayMultipleNameWorker extends Task<Integer> {

    public static Process pb;
    private Button _button;
    private List<DatabaseName> _listOfNames;

    int targetVolDB = -15;
    double volDiff = 0;

    public AudioPlayMultipleNameWorker(List<DatabaseName> listOfNames, Button button) {
        _listOfNames = listOfNames;
        _button = button;
    }

    @Override
    protected Integer call() throws Exception {
        for (DatabaseName db : _listOfNames) {
            String detectVolume = String.format("ffmpeg -y -i" + " '" + db.getPathToRecording() + "'" + " -filter:a volumedetect -f null /dev/null 2>&1 | grep mean_volume");
            Process getVol = new ProcessBuilder("bash","-c",detectVolume).start();
            getVol.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(getVol.getInputStream()));
            String output = br.readLine();

            String[] arr = output.split("\\s");
            String index = arr[4];
            double meanVol = Double.valueOf(index);

            volDiff = targetVolDB - meanVol;

            String normalisedCommand = String.format("ffmpeg -y -i " + "'" + db.getPathToRecording() + "'" + " -filter:a " + " \"volume=" + volDiff + "dB\" " + "'" + System.getProperty("user.dir") + "/Temp/temp.wav"  + "'"  );
            Process normaliseVol = new ProcessBuilder("bash", "-c",normalisedCommand).start();
            if (normaliseVol.waitFor() != 0) {
                return 1;
            }

            String trimCommand = String.format(
                    "ffmpeg -y -hide_banner -i  " + "'" + System.getProperty("user.dir") + "/Temp/temp.wav"
                            + "'" + " -af silenceremove=0:0:0:1:5:-40dB "
                            + "'" + System.getProperty("user.dir")
                            + "/Temp/temp.wav" + "'"
            );
            pb = new ProcessBuilder("bash", "-c", trimCommand).start();
            if (pb.waitFor() != 0) {
                return 2;
            }



            String ffmpegCommand = String.format("ffplay -nodisp -autoexit \'%s\'", System.getProperty("user.dir") + "/Temp/temp.wav");
            try {
                pb = new ProcessBuilder("bash", "-c", ffmpegCommand).start();
                if (pb.waitFor() != 0) {
                    return 3;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    @Override
    protected void succeeded() {
        _button.setText("Listen");
    }

}

