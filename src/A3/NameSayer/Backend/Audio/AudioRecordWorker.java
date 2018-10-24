package A3.NameSayer.Backend.Audio;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        String ffmpegCommand = String.format("ffmpeg -y -f alsa -ac 1 -ar 44100 -i default -t 5 -strict -2 \'%s\'", _recordingPath);
        String detectVolume = String.format("ffmpeg -y -i" + " '" + _recordingPath + "'" + " -filter:a volumedetect -f null /dev/null 2>&1 | grep mean_volume");
        try {
            pb = new ProcessBuilder("bash", "-c", ffmpegCommand).start();
            pb.waitFor();
            String trimCommand = String.format("ffmpeg -y -hide_banner -i  " + "'" + _recordingPath + "'" + " -af silenceremove=0:0:0:1:5:-30dB " + "'"+ _recordingPath + "'");
            Process trimProcess = new ProcessBuilder("bash", "-c", trimCommand).start();
            trimProcess.waitFor();

            Process getVol = new ProcessBuilder("bash","-c",detectVolume).start();
            getVol.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(getVol.getInputStream()));
            String output = br.readLine();

            String[] arr = output.split("\\s");
            String index = arr[4];
            double meanVol = Double.valueOf(index);

            volDiff = targetVolDB - meanVol;

            String normalisedCommand = String.format("ffmpeg -y -i " + "'" + _recordingPath + "'" + " -filter:a " + " \"volume=" + volDiff + "dB\" " + "'" + _recordingPath + "'"  );
            Process normaliseVol = new ProcessBuilder("bash", "-c",normalisedCommand).start();
            return normaliseVol.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
