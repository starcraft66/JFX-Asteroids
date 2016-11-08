package co.tdude.asteroids;

/**
 * Created by tristan on 2016-11-03.
 */
public class Bullet extends Sprite {
    protected double rotation;

    public Bullet() {
        super();
        this.setImage("file:assets/bullet.png");
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
