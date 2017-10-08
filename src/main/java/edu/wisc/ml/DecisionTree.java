package edu.wisc.ml;

import java.util.List;

public class DecisionTree {

    private DataSet ds;


    public static void main(String []args) {
        FileReaderHelper fr = new FileReaderHelper();
        List<String> result = null;
        try {
             result = fr.readData("/Users/rohitsd/workspace/machinelearning/diabetes_train.arff");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        DataSet ds = new DataSet();
        ds.addData(result);

        // Train DT.
        DecisionTree dt = new DecisionTree();
        System.out.println(dt.infoGain(ds));
        int pos = 9, neg = 5, total = 14;
        // double infoGain = -(pos*1.0/total)*log2(pos, total) -(neg*1.0/total)*log2(neg, total);
        // System.out.println("infogain = "+infoGain + " log2="+log2(pos,total));


    }

    public double infoGain(DataSet ds) {
        int pos = 0, neg = 0;
        for (DataInstance di : ds.getDataInstances()) {
           if (di.isOutputPositive()) {
               pos++;
           }
           else {
               neg++;
           }
        }

        int total = pos+neg;
        double infoGain = -(pos*1.0/total)*log2(pos, total) - (neg*1.0/total)*log2(neg, total);
        return infoGain;
    }

    static double log2(double num, double den) {
        double val = (1.0*num)/(1.0*den);
        return (Math.log(val))/Math.log(2.0);
    }
}
