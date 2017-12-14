package fuzzycar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.neuroph.core.data.DataSet;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public class ParkingLot extends JPanel {
    
    private static ParkingLot singleton;
    private final SimulatedCar simulatedCar;
    private final FuzzyDriver driver;
    private final Rectangle loadingZone;
    private static final int LOADING_ZONE_WIDTH = 40;
    private CarThread carThread;
    private NetworkCarDriver networkDriver;
    private NetworkTrainer trainer;
    private NeuralCarPredictor neuralNetwork;
    
    synchronized public static ParkingLot getInstance() {
        if (singleton == null) {
            singleton = new ParkingLot();
        }
        return singleton;
    }
    
    public ParkingLot() {
        super();
        
        simulatedCar = new SimulatedCar(150, 550, 35, 0, 45);
        this.loadingZone = new Rectangle(50 - LOADING_ZONE_WIDTH / 2, 0, LOADING_ZONE_WIDTH, 100); // Initial position: 50
        driver = new FuzzyDriver("fuzzyCar.fcl");
        networkDriver = new NetworkCarDriver();
        UserListener listener = new UserListener();
        this.addKeyListener(listener);
        this.addMouseListener(listener);
        this.addMouseWheelListener(listener);
    }
    
    public void turnLeft() {
        simulatedCar.turnWheel(-1);
        repaint();
    }
    
    public void turnRight() {
        simulatedCar.turnWheel(1);
        repaint();
    }
    
    public void moveForward() {
        simulatedCar.moveForward();
        repaint();
    }
    
    public void moveBackward() {
        simulatedCar.moveBackward();
        repaint();
    }
    
    public void setLoadingZone(int x) {
        this.loadingZone.setBounds(x - LOADING_ZONE_WIDTH / 2, 0, LOADING_ZONE_WIDTH, 100);
        repaint();
    }
    
    public void setCarPosition(int x, int y) {
        this.simulatedCar.setX(x);
        this.simulatedCar.setY(y);
        repaint();
    }
    
    public void turnCarOrientation(double angle) {
        this.simulatedCar.setPhi(this.simulatedCar.getPhi() + angle);
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.yellow);
        g.fillRect((int) loadingZone.getX(), (int) loadingZone.getY(), (int) loadingZone.getWidth(), (int) loadingZone.getHeight());
        
        g.setColor(Color.BLACK);
        simulatedCar.paint(g);
    }
    
    void trainNetwork() {
        trainer = new NetworkTrainer();
        trainer.start();
    }
    
    void parkWithNetwork() {
        
        List<List<Double>> data = trainer.getData();
        neuralNetwork = new NeuralCarPredictor(0.5, data.get(0), data.get(1), data.get(2));
        DataSet dataSet = neuralNetwork.prepareData();
        neuralNetwork.trainNetwork(dataSet);
        
        networkDriver.start();
    }
    
    private class NetworkTrainer extends Thread {
        
        List<Double> positionData;
        List<Double> orientationData;
        List<Double> thetaData;
        
        public NetworkTrainer() {
            positionData = new ArrayList<>();
            orientationData = new ArrayList<>();
            thetaData = new ArrayList<>();
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                
                simulatedCar.setX(Math.random() * getWidth());
                simulatedCar.setY(200 + Math.random() * (getHeight() - 200));
                simulatedCar.setTheta(Math.random() * 30);
                simulatedCar.setPhi(Math.random() * 360);
                
                while (!loadingZone.contains(simulatedCar.getRectInLoadingPosition())) {
                    positionData.add(loadingZone.getCenterX() - simulatedCar.getX());
                    orientationData.add(simulatedCar.getPhi());
                    double theta = driver.getResult(loadingZone.getCenterX() - simulatedCar.getX(), simulatedCar.getPhi());
                    thetaData.add(theta);
                    
                    simulatedCar.setTheta(theta);
                    simulatedCar.moveBackward();
                    repaint();
                    
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        Logger.getLogger(ParkingLot.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        }
        
        public List<List<Double>> getData() {
            List<List<Double>> data = new ArrayList<>();
            
            data.add(positionData);
            data.add(orientationData);
            data.add(thetaData);
            
            return data;
        }
    }
    
    private class NetworkCarDriver extends Thread {
        
        @Override
        public void run() {
            while (!loadingZone.contains(simulatedCar.getRectInLoadingPosition())) {
                simulatedCar.setTheta(neuralNetwork.calculate(loadingZone.getCenterX() - simulatedCar.getX(), simulatedCar.getPhi()));
                simulatedCar.moveBackward();
                repaint();
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Logger.getLogger(ParkingLot.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
    
    private class CarThread extends Thread {
        
        @Override
        public void run() {
            while (true) {
                synchronized (this) {
                    simulatedCar.setTheta(driver.getResult(loadingZone.getCenterX() - simulatedCar.getX(), simulatedCar.getPhi()));
                    simulatedCar.moveBackward();
                    repaint();
                    
                    if (loadingZone.contains(simulatedCar.getRectInLoadingPosition())) {
                        
                        System.out.println("O carro parou com sucesso na vaga");
                        try {
                            this.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ParkingLot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Logger.getLogger(ParkingLot.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        }
    }
    
    public void start() {
        if (carThread == null) {
            carThread = new CarThread();
            carThread.start();
        } else {
            synchronized (carThread) {
                carThread.notifyAll();
            }
        }
    }
}
