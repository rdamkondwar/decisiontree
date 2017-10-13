package edu.wisc.ml;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DTRunner {

    public List<DataInstance> getRandomlySelectedDataInstances(List<DataInstance> diList, int percent) {
        int size = diList.size();
        int newSize = (size*percent)/100;

        List<DataInstance> result = new ArrayList<DataInstance>(newSize);

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < newSize; i++) {
            int index = random.nextInt(diList.size());
            DataInstance dataInstance = diList.remove(index);
            result.add(dataInstance);
        }

        // System.out.println("Debug: datasize:"+result.size());

        return result;
    }

    public void runDT(String arg0, String arg1, String arg2, String arg3) throws Exception {
        FileReaderHelper fr = new FileReaderHelper();
        List<String> result = null;
        List<String> testSetLines = null;
        Integer m = null;
        int percent = 100;

        result = fr.readData(arg0);
        testSetLines = fr.readData(arg1);
        m = Integer.parseInt(arg2);

        if (null != arg3) {
            percent = Integer.parseInt(arg3);
        }

        if (percent > 100) {
            percent = 100;
        }

        DataSet ds = new DataSet();
        // ds.addData(getRandomlySelectedDataInstances(result, percent));
        ds.addData(result);

        // System.out.println("Hello!");
        ds.setDataInstances(getRandomlySelectedDataInstances(ds.getDataInstances(), percent));

        DataSet testSet = new DataSet();
        testSet.addData(testSetLines);

        // Train DT.
        DecisionTree dt = new DecisionTree(ds, m, "negative");

        // dt.printDecisionTree(0);

        // System.out.println("<Predictions for the Test Set Instances>");
        // int printIdx = 1;
        int total = 0, correctPredictions = 0;
        for (DataInstance di : testSet.getDataInstances()) {
            String predicted = dt.parseDecisionTree(di);
            // printIdx++;
            // System.out.println(printIdx+": Actual: "+di.outputVal+" Predicted: "+predicted);
            if (di.outputVal.equals(predicted)) {
                correctPredictions++;
            }
            total++;
        }

        System.out.println("Number of correctly classified: "+correctPredictions+" Total number of test instances: "+total + " Accuracy: "+((100.0*correctPredictions)/total));
    }

    public static void main(String []args) {
        DTRunner dtRunner = new DTRunner();
        try {
            if (args.length == 4) {
                for(int i = 0; i < 10; i++) {
                    System.out.print(i+" ");
                    dtRunner.runDT(args[0], args[1], args[2], args[3]);
                }
            }
            else if (args.length == 3){
                for(int i=0; i < 10; i++) {
                    dtRunner.runDT(args[0], args[1], args[2], null);
                }
            }
            else {
                System.out.println("Invalid Arguments!");
                System.out.println("Usage: DTRunner <path_to_train_set> <path_to_test_set> <m_value> <percent>");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
