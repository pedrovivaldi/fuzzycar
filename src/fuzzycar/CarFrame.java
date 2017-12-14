package fuzzycar;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public class CarFrame extends JFrame {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 650;

    public CarFrame(ParkingLot carPanel) {
        super("Fuzzy Car");

        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        carPanel.setBorder(border);

        this.getContentPane().add(carPanel);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        UserListener listener = new UserListener();
        this.addKeyListener(listener);
        this.addMouseListener(listener);
        this.addMouseWheelListener(listener);
    }
}
