package project;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The sound handler class functions as a container for all sound related functionality.
 * Sound loading is hard coded into the constructor as more advanced measures aren't needed for the surrounding setting.
 * A logical feature extension is playlist functionality for the main song player, to add multiple soundtracks.
 */
public class SoundHandler {

    MediaPlayer song1Player;
    List<MediaPlayer> dropSfx = new ArrayList<>();
    private boolean muted = false;

    /**
     * Constructor loads sound files into media player objects, and sets some parameters on these objects.
     */
    public SoundHandler(){
        /*
         * Exception handling should be implemented on the individual sound loads.
         * Currently a file not found will result in subsequent loads not being attempted.
         */
        try{
            URL song1URL = getClass().getResource("/sound/song1.mp3");
            song1Player = new MediaPlayer(new Media(song1URL.toString()));
            song1Player.setVolume(0.25);
            song1Player.setOnEndOfMedia(() -> {
                song1Player.seek(Duration.ZERO);
                song1Player.play();
            });
            URL dropSfx1URL = getClass().getResource("/sound/sfx1.mp3");
            URL dropSfx2URL = getClass().getResource("/sound/sfx2.mp3");
            URL dropSfx3URL = getClass().getResource("/sound/sfx3.mp3");
            URL dropSfx4URL = getClass().getResource("/sound/sfx4.mp3");
            URL dropSfx5URL = getClass().getResource("/sound/sfx5.mp3");
            URL dropSfx6URL = getClass().getResource("/sound/sfx6.mp3");
            URL dropSfx7URL = getClass().getResource("/sound/sfx7.mp3");
            URL dropSfx8URL = getClass().getResource("/sound/sfx8.mp3");
            dropSfx.add(new MediaPlayer(new Media(dropSfx1URL.toString())));
            dropSfx.add(new MediaPlayer(new Media(dropSfx2URL.toString())));
            dropSfx.add(new MediaPlayer(new Media(dropSfx3URL.toString())));
            dropSfx.add(new MediaPlayer(new Media(dropSfx4URL.toString())));
            dropSfx.add(new MediaPlayer(new Media(dropSfx5URL.toString())));
            dropSfx.add(new MediaPlayer(new Media(dropSfx6URL.toString())));
            dropSfx.add(new MediaPlayer(new Media(dropSfx7URL.toString())));
            dropSfx.add(new MediaPlayer(new Media(dropSfx8URL.toString())));
            for(MediaPlayer sfx : dropSfx){
                sfx.setVolume(0.6);
            }
        }
        catch(Exception filesNotFound) {
            System.out.println("Files missing, some sounds may not play.");
        }
    }

    /**
     * Mutes all MediaPlayer objects, without pausing them.
     */
    public void toggleMute(){
        muted = !muted;
        for(MediaPlayer sfx : dropSfx){
            sfx.setMute(muted);
        }
        song1Player.setMute(muted);
    }

    /**
     * Plays a random sound from the SFX array.
     */
    public void playSFX(){
        Random random = new Random();
        MediaPlayer sfx = dropSfx.get(random.nextInt(dropSfx.size()));
        sfx.play();
        sfx.seek(Duration.ZERO);
    }

    /*
     * ---------------------------------------------
     * Wrapper methods for the song1Player instance.
     * ---------------------------------------------
     */
    public void playMusic(){
        song1Player.play();
    }

    public void pauseMusic(){
        song1Player.pause();
    }

    public void stopMusic(){
        song1Player.stop();
    }
}