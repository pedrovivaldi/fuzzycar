package fuzzycar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public class SimulatedCar extends Car {

    public static final int CAR_WIDTH = 20;
    public static final int WHEEL_RADIUS = 3;

    public SimulatedCar(int x, int y, int r, double theta, double phi) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }

    @Override
    protected void moveForward() {
        phi = phi - theta;
        if (Math.abs(phi) > 360) {
            phi = phi % 360;
        }
        x = x - 15 * Math.cos(Math.toRadians(phi));
        y = y + 15 * Math.sin(Math.toRadians(phi));
    }

    @Override
    protected void moveBackward() {
        phi = phi + theta;
        x = x + 15 * Math.cos(Math.toRadians(phi));
        y = y - 15 * Math.sin(Math.toRadians(phi));
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform old = g2d.getTransform();
        double rotationCenterX = x;
        double rotationCenterY = y;

        g.setColor(Color.black);

        g2d.rotate(Math.toRadians(-phi), rotationCenterX, rotationCenterY);

        // Draw car
        g.drawRect((int) (x - r), (int) (y - (CAR_WIDTH / 2)), (int) r, (int) CAR_WIDTH);

        g.drawLine((int) (x - r), (int) y, (int) (x - (r / 2)), (int) y);

        // Draw back wheels
        g.drawLine((int) (x - (r / 4) - WHEEL_RADIUS), (int) (y - ((CAR_WIDTH / 2) + 1)), (int) (x - (r / 4) + WHEEL_RADIUS), (int) (y - ((CAR_WIDTH / 2) + 1)));
        g.drawLine((int) (x - (r / 4) - WHEEL_RADIUS), (int) (y + ((CAR_WIDTH / 2) + 1)), (int) (x - (r / 4) + WHEEL_RADIUS), (int) (y + ((CAR_WIDTH / 2) + 1)));

        g.setColor(Color.lightGray);

        g.drawLine((int) (x - (r / 4) - WHEEL_RADIUS), (int) (y - ((CAR_WIDTH / 2) + 2)), (int) (x - (r / 4) + WHEEL_RADIUS), (int) (y - ((CAR_WIDTH / 2) + 2)));
        g.drawLine((int) (x - (r / 4) - WHEEL_RADIUS), (int) (y + ((CAR_WIDTH / 2) + 2)), (int) (x - (r / 4) + WHEEL_RADIUS), (int) (y + ((CAR_WIDTH / 2) + 2)));

        AffineTransform old2 = g2d.getTransform();

        // Draw front wheels
        g2d.rotate(Math.toRadians(theta), x - 3 * (r / 4), y - ((CAR_WIDTH / 2) + 1));

        g.setColor(Color.black);

        g.drawLine((int) (x - 3 * (r / 4) - WHEEL_RADIUS), (int) (y - ((CAR_WIDTH / 2) + 1)), (int) (x - 3 * (r / 4) + WHEEL_RADIUS), (int) (y - ((CAR_WIDTH / 2) + 1)));

        g2d.setTransform(old2);

        g2d.rotate(Math.toRadians(theta), x - 3 * (r / 4), y + ((CAR_WIDTH / 2) + 1));

        g.drawLine((int) (x - 3 * (r / 4) - WHEEL_RADIUS), (int) (y + ((CAR_WIDTH / 2) + 1)), (int) (x - 3 * (r / 4) + WHEEL_RADIUS), (int) (y + ((CAR_WIDTH / 2) + 1)));

        g2d.setTransform(old);
    }

    public Rectangle getRectInLoadingPosition() {
        return new Rectangle((int) (x - (CAR_WIDTH / 2)), (int) y, (int) CAR_WIDTH, (int) r);
    }
}
