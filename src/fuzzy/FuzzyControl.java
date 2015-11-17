package fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyControl {

	public static void main(String[] args) throws Exception {
        // Load from 'FCL' file
        String fileName = "lineTracker.fcl";
        FIS fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Show 
        JFuzzyChart.get().chart(fis.getFunctionBlock("lineTracker"));;

        // Set inputs
        fis.setVariable("diferenca", 0);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable tip = fis.getFunctionBlock("lineTracker").getVariable("forca");
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);

        // Print ruleSet
        System.out.println(fis);
    }
}
