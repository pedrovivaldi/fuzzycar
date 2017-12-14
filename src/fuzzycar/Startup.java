package fuzzycar;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public class Startup {

    public static void main(String args[]) {
        CarFrame carFrame = new CarFrame(ParkingLot.getInstance());
    }
}
