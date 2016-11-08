package co.tdude.asteroids;

/**
 * Created by tristan on 2016-11-03.
 */
public class Spaceship extends Sprite {
    protected double rotation;

    public Spaceship() {
        super();
        this.setImage("file:///Users/tristan/Documents/java/Asteroids/assets/ship.png");
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

}
