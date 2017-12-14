package fuzzycar;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public class UserListener implements KeyListener, MouseListener, MouseWheelListener {

    private final Set<Integer> pressed = new HashSet<>();

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        pressed.add(e.getKeyCode());
        for (int key : pressed) {
            switch (key) {
                case KeyEvent.VK_LEFT:
                    ParkingLot.getInstance().turnLeft();
                    break;
                case KeyEvent.VK_UP:
                    ParkingLot.getInstance().moveForward();
                    break;
                case KeyEvent.VK_RIGHT:
                    ParkingLot.getInstance().turnRight();
                    break;
                case KeyEvent.VK_DOWN:
                    ParkingLot.getInstance().moveBackward();
                    break;
                case KeyEvent.VK_1:
                    ParkingLot.getInstance().start();
                    break;
                case KeyEvent.VK_2:
                    ParkingLot.getInstance().trainNetwork();
                    break;
                case KeyEvent.VK_3:
                    ParkingLot.getInstance().parkWithNetwork();
                    break;
            }
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        pressed.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            ParkingLot.getInstance().setLoadingZone(e.getX());
        } else {
            ParkingLot.getInstance().setCarPosition(e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            ParkingLot.getInstance().turnCarOrientation(5.0);
        } else {
            ParkingLot.getInstance().turnCarOrientation(-5.0);

        }
    }

}
