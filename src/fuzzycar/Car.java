package fuzzycar;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public abstract class Car {

    protected double x;
    protected double y;
    protected double r;
    protected double theta;
    protected double phi;

    protected abstract void moveForward();

    protected abstract void moveBackward();

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        if (Math.abs(phi) > 360.0) {
            this.phi = phi % 360;
        } else {
            this.phi = phi;
        }

    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    // angle > 0 = right
    // angle < 0 = left
    public void turnWheel(double angle) {
        if (angle != 0.0) {
            theta += angle;
            if (theta > 0.0) {
                theta = Math.min(theta, 30.0);
            } else {
                theta = Math.max(theta, -30.0);
            }
        }
    }

    @Override
    public String toString() {
        return "Car{" + "x=" + x + ", y=" + y + ", r=" + r + ", theta=" + theta + ", phi=" + phi + '}';
    }
}
