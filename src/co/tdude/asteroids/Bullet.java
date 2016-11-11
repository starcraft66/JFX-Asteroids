package co.tdude.asteroids;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * Created by tristan on 2016-11-03.
 */
public class Bullet extends Sprite {
    protected double rotation;
    protected MediaPlayer mediaPlayer;

    public Bullet() {
        super();
        this.setImage("file:assets/bullet.png");
        Media sound = new Media(new File("assets/shot.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
