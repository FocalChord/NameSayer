package A3.NameSayer.Backend.Audio;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class deals with the recording functionality of nameSayer
 */

public class AudioRecordWorker extends Task<Integer> {

    public static Process pb;
    private String _recordingPath;

    int targetVolDB = -15;
    double volDiff = 0;

    public AudioRecordWorker(String recordingPath) {
        _recordingPath = recordingPath;
    }


    @Override
    protected Integer call() throws Exception {
        //bash command which deals with audio recording
        String ffmpegCommand = String.format("ffmpeg -y -f alsa -ac 1 -ar 44100 -i default -t 5 -strict -2 \'%s\'", _recordingPath);
        //bash command to retrieve the mean volume for normalising
        String detectVolume = String.format("ffmpeg -y -i" + " '" + _recordingPath + "'" + " -filter:a volumedetect -f null /dev/null 2>&1 | grep mean_volume");
        try {
            //Record Audio
            pb = new ProcessBuilder("bash", "-c", ffmpegCommand).start();
            pb.waitFor();

            //Trim Audio
            String trimCommand = String.format("ffmpeg -y -hide_banner -i  " + "'" + _recordingPath + "'" + " -af silenceremove=0:0:0:1:5:-20dB " + "'"+ _recordingPath + "'");
            Process trimProcess = new ProcessBuilder("bash", "-c", trimCommand).start();
            trimProcess.waitFor();

            //Get Volume of recording
            Process getVol = new ProcessBuilder("bash","-c",detectVolume).start();
            getVol.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(getVol.getInputStream()));
            String output = br.readLine();

            //Parsing the mean volume into a format we can deal with
            String[] arr = output.split("\\s");
            String index = arr[4];
            double meanVol = Double.valueOf(index);

            //Calculate the difference between target and mean volume
            volDiff = targetVolDB - meanVol;

            //Normalise the audio we have recorded
            String normalisedCommand = String.format("ffmpeg -y -i " + "'" + _recordingPath + "'" + " -filter:a " + " \"volume=" + volDiff + "dB\" " + "'" + _recordingPath + "'"  );
            Process normaliseVol = new ProcessBuilder("bash", "-c",normalisedCommand).start();
            return normaliseVol.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
