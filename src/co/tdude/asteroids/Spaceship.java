package co.tdude.asteroids;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;

/**
 * Created by tristan on 2016-11-03.
 */
public class Spaceship extends Sprite {
    protected double rotation;
    protected boolean isInvulnerable;
    private long killNanoTime;
    private Image idleImage;
    private Image boostImage;
    private Image blankImage;
    private boolean isBoosting;

    public Spaceship() {
        super();
        this.idleImage = new Image("file:assets/ship_idle.png");
        this.boostImage = new Image("file:assets/ship.png");
        this.blankImage = new Image("file:assets/blank.png");
        this.setImage(idleImage);
        this.isInvulnerable = false;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        while (rotation > 360.0) {
            rotation -= 360.0;
        }
        while (rotation < 0) {
            rotation += 360;
        }
        this.rotation = rotation;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public void kill() {
        this.isInvulnerable = true;
        this.killNanoTime = System.nanoTime();
        new AnimationTimer() {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - killNanoTime) / 1000000000.0;
                if ( t >= 3) {
                    isInvulnerable = false;
                    this.stop();
                }
            }
        }.start();
    }

    public void boost() {
        double radRotation = Math.toRadians(this.getRotation());
        double x = 5 * -Math.cos(radRotation);
        double y = 5 * -Math.sin(radRotation);
        //Booster
        this.addVelocity(x, y);
        this.isBoosting = true;
    }

    public boolean isBoosting() {
        return isBoosting;
    }

    @Override
    public void update(double time) {
        super.update(time);
        setStateImage();
    }

    public void setStateImage() {
        if (this.isBoosting()) {
            if (this.getImage() != this.boostImage) {
                this.setImage(boostImage);
            }
            this.isBoosting = false;
        } else {
            this.setImage(idleImage);
        }
    }

    public void setBlankImage() {
        this.setImage(this.blankImage);
    }
}
