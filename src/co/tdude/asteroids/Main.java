package co.tdude.asteroids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends Application {

    static Scene mainScene;
    static GraphicsContext graphicsContext;
    static int WIDTH = 800;
    static int HEIGHT = 600;

    static HashSet<String> currentlyActiveKeys;

    static int ASTEROIDS = 10;
    static CopyOnWriteArrayList<Asteroid> asteroids;
    static CopyOnWriteArrayList<Bullet> bullets;

    static Spaceship spaceship;
    static double SPACESHIPACCELL = 150.0;
    static double TURNRATE = 4.0;

    private static long lastLoop;
    private static long lastShoot;

    @Override
    public void start(Stage theStage) throws Exception{
        theStage.setTitle( "Asteroids" );

        Group root = new Group();
        mainScene = new Scene( root );
        theStage.setScene( mainScene );

        Canvas canvas = new Canvas( WIDTH, HEIGHT );
        root.getChildren().add( canvas );

        graphicsContext = canvas.getGraphicsContext2D();

        lastShoot = 0;

        //Handle currentlyActiveKeys

        asteroids = new CopyOnWriteArrayList<Asteroid>();
        bullets = new CopyOnWriteArrayList<Bullet>();
        for(int i = 0; i < ASTEROIDS; i++) {
            asteroids.add(new Asteroid());
            Asteroid a = asteroids.get(i);
            Random r = new Random();
            //TODO: Clean up this spaghetti code
            a.setPosition(r.nextInt(WIDTH - (int)a.getBoundary().getWidth()) + a.getBoundary().getWidth(), r.nextInt(HEIGHT - (int)a.getBoundary().getHeight()) + a.getBoundary().getHeight());
            boolean negativex = r.nextBoolean();
            boolean negativey = r.nextBoolean();
            int velocityx = r.nextInt(100) + 150;
            int velocityy = r.nextInt(100) + 150;
            a.setVelocity(negativex ? -1 * velocityx : velocityx, negativey ? -1 * velocityy : velocityy);
        }

        spaceship = new Spaceship();

        currentlyActiveKeys = new HashSet<String>();

        mainScene.setOnKeyPressed(e ->
        {
            String code = e.getCode().toString();

            // only add once... prevent duplicates
            if (!currentlyActiveKeys.contains(code))
                currentlyActiveKeys.add(code);
        });

        mainScene.setOnKeyReleased(e ->
        {
            String code = e.getCode().toString();
            currentlyActiveKeys.remove(code);
        });


        /**
         * Main "game" loop
         */
        lastLoop = System.nanoTime();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - lastLoop) / 1000000000.0;
                tickAndRender(t);
            }
        }.start();

        theStage.show();
    }

    private static void tickAndRender(double time)
    {
        // clear canvas
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);

        for (Asteroid a : asteroids) {
            a.update(time);
        }
        for (Bullet b : bullets) {
            b.update(time * 3);
        }

        spaceship.update(time);


        if (currentlyActiveKeys.contains("LEFT"))
        {
            spaceship.setRotation(spaceship.getRotation() - TURNRATE);
            //Rotate left
        }
        if (currentlyActiveKeys.contains("RIGHT"))
        {
            spaceship.setRotation(spaceship.getRotation() + TURNRATE);
            //Rotate right
        }
        if (currentlyActiveKeys.contains("UP"))
        {
            double radRotation = Math.toRadians(spaceship.getRotation());
            double x = SPACESHIPACCELL * -Math.cos(radRotation);
            double y = SPACESHIPACCELL * -Math.sin(radRotation);
            //Booster
            spaceship.setVelocity(x, y);
        } else {
            spaceship.setVelocity(0, 0);
        }

        if (currentlyActiveKeys.contains("SPACE"))
        {
            if (System.nanoTime() - lastShoot > 500000000/*Half a second*/) {
                //Shoot
                double radRotation = Math.toRadians(spaceship.getRotation());
                double x = 25 * -Math.cos(radRotation);
                double y = 25 * -Math.sin(radRotation);
                double accelx = SPACESHIPACCELL * -Math.cos(radRotation);
                double accely = SPACESHIPACCELL * -Math.sin(radRotation);
                Bullet b = new Bullet();
                b.setRotation(spaceship.getRotation());
                b.setPosition(x + spaceship.getBoundary().getMinX() + (spaceship.getBoundary().getWidth() / 4), y + spaceship.getBoundary().getMinY() + (spaceship.getBoundary().getHeight() / 4));
                //b.setVelocity(accelx, accely);
                bullets.add(b);
                lastShoot = System.nanoTime();
            }
        }


        for (Asteroid a : asteroids) {
            for (Bullet b : bullets) {
                if (a.intersects(b)) {
                    bullets.remove(b);
                    asteroids.remove(a);
                }
            }
        }

        for (Bullet b : bullets) {
            double radRotation = Math.toRadians(b.getRotation());
            double accelx = SPACESHIPACCELL * -Math.cos(radRotation);
            double accely = SPACESHIPACCELL * -Math.sin(radRotation);
            b.setVelocity(accelx, accely);
            drawRotatedImage(graphicsContext, b.getImage(), b.getRotation(), b.getBoundary().getMinX(), b.getBoundary().getMinY());
            checkDespawnBullet(b);
        }

        for (Asteroid a : asteroids) {
            a.render(graphicsContext);
        }

        //Draw the rotated space ship
        drawRotatedImage(graphicsContext, spaceship.getImage(), spaceship.getRotation(), spaceship.getBoundary().getMinX(), spaceship.getBoundary().getMinY());

        //wrap around
        for (Asteroid a : asteroids) {
            wrapSprite(a);
        }
        wrapSprite(spaceship);


        lastLoop = System.nanoTime();
    }

    public static void wrapSprite(Sprite a) {
        if (a.getBoundary().getMinX() > WIDTH) {
            a.setPosition(0 - a.getBoundary().getWidth(), a.getBoundary().getMinY());
        } else if (a.getBoundary().getMaxX() < 0) {
            a.setPosition(WIDTH, a.getBoundary().getMinY());
        }
        if (a.getBoundary().getMinY() > HEIGHT) {
            a.setPosition(a.getBoundary().getMinX(), 0 - a.getBoundary().getHeight());
        } else if (a.getBoundary().getMaxY() < 0) {
            a.setPosition(a.getBoundary().getMinX(), HEIGHT);
        }
    }

    public static void checkDespawnBullet(Bullet b) {
        if (b.getBoundary().getMinX() > WIDTH) {
            bullets.remove(b);
        } else if (b.getBoundary().getMaxX() < 0) {
            bullets.remove(b);
        }
        if (b.getBoundary().getMinY() > HEIGHT) {
            bullets.remove(b);
        } else if (b.getBoundary().getMaxY() < 0) {
            bullets.remove(b);
        }
    }

    /**
     * Sets the transform for the GraphicsContext to rotate around a pivot point.
     *
     * @param gc the graphics context the transform to applied to.
     * @param angle the angle of rotation.
     * @param px the x pivot co-ordinate for the rotation (in canvas co-ordinates).
     * @param py the y pivot co-ordinate for the rotation (in canvas co-ordinates).
     */
    private static void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    /**
     * Draws an image on a graphics context.
     *
     * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
     *   (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
     *
     * @param gc the graphics context the image is to be drawn on.
     * @param angle the angle of rotation.
     * @param tlpx the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
     * @param tlpy the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
     */
    private static void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save(); // saves the current state on stack, including the current transform
        rotate(gc, angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.drawImage(image, tlpx, tlpy);
        gc.restore(); // back to original state (before rotation)
    }


    public static void main(String[] args) {
        launch(args);
    }
}
