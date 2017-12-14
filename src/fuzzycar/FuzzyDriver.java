package fuzzycar;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 *
 * @author Pedro Vivaldi <pedro.cotta.vivaldi@gmail.com>
 */
public class FuzzyDriver {

    FIS fis;

    public FuzzyDriver(String fileName) {
        // Load from 'FCL' file
        fis = FIS.load(fileName, true);

        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName + "'");
        }

        JFuzzyChart.get().chart(fis);
    }

    public double getResult(double error, double phi) {
        fis.setVariable("position_error", error);
        fis.setVariable("orientation", phi);

        fis.evaluate();

        Variable angle = fis.getVariable("wheel");

        return angle.getDefuzzifier().defuzzify();
    }
}
