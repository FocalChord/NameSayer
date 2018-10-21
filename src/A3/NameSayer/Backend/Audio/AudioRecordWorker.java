package A3.NameSayer.Backend.Audio;

import javafx.concurrent.Task;

import java.io.IOException;

public class AudioRecordWorker extends Task<Integer> {

    public static Process pb;
    private String _recordingPath;

    public AudioRecordWorker(String recordingPath) {
        _recordingPath = recordingPath;
    }


    @Override
    protected Integer call() throws Exception {
        String ffmpegCommand = String.format("ffmpeg -y -f alsa -ac 1 -ar 44100 -i default -t 5 -strict -2 \'%s\'", _recordingPath);
        try {
            pb = new ProcessBuilder("bash", "-c", ffmpegCommand).start();
            pb.waitFor();
            String trimCommand = String.format("ffmpeg -y -hide_banner -i  " + "'" + _recordingPath + "'" + " -af silenceremove=0:0:0:1:5:-48dB " + "'"+ _recordingPath + "'");
            System.out.println(trimCommand);
            Process trimProcess = new ProcessBuilder("bash", "-c", trimCommand).start();
            return trimProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
