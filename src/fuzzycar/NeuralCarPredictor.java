package fuzzycar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public class NeuralCarPredictor {

    private final double learningRate;
    private List<Double> positionData;
    private List<Double> orientationData;
    private List<Double> thetaData;

    private double positionMax = 0;
    private double positionMin = Double.MAX_VALUE;
    private double orientationMax = 0;
    private double orientationMin = Double.MAX_VALUE;
    private double thetaMax = 0;
    private double thetaMin = Double.MAX_VALUE;

    private final String neuralNetworkModelFilePath = "stockPredictor.nnet";

    public NeuralCarPredictor(double rate, List<Double> positionData, List<Double> orientationData, List<Double> thetaData) {
        this.learningRate = rate;
        this.positionData = new ArrayList<>(positionData);
        this.orientationData = new ArrayList<>(orientationData);
        this.thetaData = new ArrayList<>(thetaData);
    }

    DataSet prepareData() {

        positionMax = Collections.max(positionData);
        positionMin = Collections.min(positionData);

        orientationMax = Collections.max(orientationData);
        orientationMin = Collections.min(orientationData);

        thetaMax = Collections.max(thetaData);
        thetaMin = Collections.min(thetaData);

        for (int i = 0; i < positionData.size(); i++) {
            positionData.set(i, normalizeValue(positionData.get(i), positionMax, positionMin));
        }

        for (int i = 0; i < orientationData.size(); i++) {
            orientationData.set(i, normalizeValue(orientationData.get(i), orientationMax, orientationMin));
        }

        for (int i = 0; i < thetaData.size(); i++) {
            thetaData.set(i, normalizeValue(thetaData.get(i), thetaMax, thetaMin));
        }

        DataSet dataSet = new DataSet(2, 1);

        for (int i = 0; i < positionData.size(); i++) {
            double trainValues[] = new double[2];
            trainValues[0] = positionData.get(i);
            trainValues[1] = orientationData.get(i);

            double expectedValue[] = new double[]{thetaData.get(i)};
            dataSet.addRow(new DataSetRow(trainValues, expectedValue));
        }

        return dataSet;
    }

    double normalizeValue(double input, double max, double min) {
        return (input - min) / (max - min) * 0.8 + 0.1;
    }

    double deNormalizeValue(double input, double max, double min) {
        return min + (input - 0.1) * (max - min) / 0.8;
    }

    void trainNetwork(DataSet trainingSet) {
        NeuralNetwork<BackPropagation> neuralNetwork = new MultiLayerPerceptron(2, 2 * 2 + 1, 1);

        int maxIterations = 1000;
        double maxError = 0.00001;

        SupervisedLearning learningRule = neuralNetwork.getLearningRule();
        learningRule.setMaxError(maxError);
        learningRule.setLearningRate(learningRate);
        learningRule.setMaxIterations(maxIterations);
        learningRule.addListener(new LearningEventListener() {
            @Override
            public void handleLearningEvent(LearningEvent learningEvent) {
                SupervisedLearning rule = (SupervisedLearning) learningEvent.getSource();
                System.out.println("Network error for iteration " + rule.getCurrentIteration() + ": " + rule.getTotalNetworkError());
            }
        });

        neuralNetwork.learn(trainingSet);
        neuralNetwork.save(neuralNetworkModelFilePath);
    }

    double calculate(double position, double orientation) {
        NeuralNetwork neuralNetwork = NeuralNetwork.createFromFile(
                neuralNetworkModelFilePath);

        neuralNetwork.setInput(normalizeValue(position, positionMax, positionMin),
                normalizeValue(orientation, orientationMax, orientationMin));

        neuralNetwork.calculate();
        double[] networkOutput = neuralNetwork.getOutput();

        return deNormalizeValue(networkOutput[0], thetaMax, thetaMin);
    }
}
