package edu.wisc.ml;

import java.util.ArrayList;
import java.util.List;

public class DecisionTree {

    // private DataSet ds;
    Attribute feature = null;
    boolean isLeafNode = false;
    Object outputLabel = null;
    List<DecisionTree> branches = new ArrayList<DecisionTree>();
    int pos = 0;
    int neg = 0;

    public static void main(String []args) throws CloneNotSupportedException {
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

        // System.out.println("Size of dataset = "+ds.getDataInstances().size());
        // Train DT.
        DecisionTree dt = new DecisionTree(ds, 2, "negative");
        // System.out.println(dt.infoGain(ds));
        dt.printDecisionTree(0);

        // System.out.println("Done");

        // Double entropy = dt.infoGain(ds);
        // Attribute attr = dt.findBestSplitCandidate(ds, entropy);
        // System.out.println("Total gain = " + dt.infoGain(ds));
    }

    public void printDecisionTree(int depth) {
        // System.out.println("Num of branches="+this.branches.size());
        /*if (this.isLeafNode) {
            String format = "%n%"+5*depth+"s";
            // System.out.println(this.outputLabel);
        }*/
        // else {
        for (int i=0; i < this.branches.size(); i++) {
            String branchName = this.feature.getBranches().get(i);
            // System.out.println(this.feature.getName() + " = " + branchName);

            DecisionTree dt = this.branches.get(i);

                StringBuilder format = new StringBuilder();
            for (int j = 0; j < depth; j++) {
                format.append("|    ");
            }
            if (dt.isLeafNode) {
                format.append(" %s [%d %d]: %s%n");
                System.out.printf(format.toString(), branchName, dt.neg, dt.pos, dt.outputLabel);
            }
            else {
                format.append(" %s [%d %d]%n");
                System.out.printf(format.toString(), branchName, dt.neg, dt.pos);
            }

            dt.printDecisionTree(depth+1);
        }
        // }
    }

    private boolean checkIfAllLabelsBelongToSameClass(DataSet ds) {
        /*int pos=0, neg=0;
        for (DataInstance di : ds.getDataInstances()) {
            if (di.isOutputPositive()) {
                pos++;
            }
            else {
                neg++;
            }
        }*/
        if (this.pos == 0 || this.neg == 0) return true;
        return false;
    }

    private Object getMajorityOutputClass(DataSet ds, Object defaultLabel) {
        List<DataInstance> diList = ds.getDataInstances();
        if (diList.size() == 0) {
            return defaultLabel;
        }

        /*int pos=0, neg=0;
        for (DataInstance di : ds.getDataInstances()) {
            if (di.isOutputPositive()) {
                pos++;
            }
            else {
                neg++;
            }
        }*/
        /*if (pos == 0 || neg == 0) {
            // Detected Stop condition.
            return diList.get(0).outputVal;
        }*/
        if (this.pos > this.neg) {
            return "positive";
        }
        else if (this.neg > this.pos) {
            return "negative";
        }

        return defaultLabel;
    }

    public DecisionTree(DataSet ds, int m, Object defaultLabel) throws CloneNotSupportedException {
        // Evaluate Stopping criteria

        ds.calculateThresholds();
        ds.createBranches();

        for (DataInstance di : ds.getDataInstances()) {
            if (di.isOutputPositive()) {
                this.pos++;
            }
            else {
                this.neg++;
            }
        }

        // Check if instances are lesser than m
        if (ds.getDataInstances().size() < m) {
            // return new DecisionTree(true, getMajorityOutputClass(ds, defaultLabel));

            this.isLeafNode = true;
            this.outputLabel = getMajorityOutputClass(ds, defaultLabel);
            // System.out.println("Debug: "+this.outputLabel);
            return;
        }

        // Check if all instances belong to same class
        if (checkIfAllLabelsBelongToSameClass(ds)) {
            // return new DecisionTree(true, ds.getDataInstances().get(0).outputVal);
            this.isLeafNode = true;
            this.outputLabel = ds.getDataInstances().get(0).outputVal;
            // System.out.println("Debug: "+this.outputLabel);
            return;
        }

        // No more features left to choose
        if (ds.getAttributes().size() == 0) {
            // return new DecisionTree(true, getMajorityOutputClass(ds, defaultLabel));
            this.isLeafNode = true;
            this.outputLabel = getMajorityOutputClass(ds, defaultLabel);
            // System.out.println("Debug: "+this.outputLabel);
            return;
        }

        Double entropy = infoGain(ds);
        this.feature = findBestSplitCandidate(ds, entropy);

        if (null == this.feature) {
            // No positive gain.
            // return new DecisionTree(true, defaultLabel);
            this.isLeafNode = true;
            this.outputLabel = defaultLabel;
            return;
        }
        // System.out.println("Selected feature="+this.feature.getName());

        for(String branch : this.feature.getBranches()) {
            // Filtered DS, m, defaultLabel
            DataSet filteredDS = getFilteredDataSetForChildBranch(ds, branch);
            this.branches.add(new DecisionTree(filteredDS, m, defaultLabel));
        }
    }

    private DataSet getFilteredDataSetForChildBranch(DataSet ds, String branch) throws CloneNotSupportedException {
        int attrIdx = getAttrIndexByName(ds, this.feature.getName());
        DataSet filteredDS = new DataSet();
        for (DataInstance di : ds.getDataInstances()) {
            if (this.feature.satisfyBranch(branch, di.getAttrValueByIndex(attrIdx))) {
                filteredDS.addDataInstance(di);
            }
        }

        List<Attribute> attrList = new ArrayList<Attribute>();
        for (Attribute attr : ds.getAttributes()) {
            if (!attr.getName().equals(this.feature.getName())) {
                attrList.add((Attribute) attr.clone());
            }
        }

        filteredDS.setAttributes(attrList);
        return filteredDS;
    }

    private int getAttrIndexByName(DataSet ds, String name) {
        int idx = 0;
        for(Attribute a : ds.getAttributes()) {
            if(a.getName().equals(name)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    public Attribute findBestSplitCandidate(DataSet ds, Double entropy) {
        Attribute attr = null;
        Double maxInfoGain = Double.MIN_VALUE;

        int idx = 0;
        for (Attribute a : ds.getAttributes()) {
            Double infoGain = entropy - infoGainForFeature(ds, idx);
            if (maxInfoGain < infoGain) {
                maxInfoGain = infoGain;
                attr = a;
            }
            idx++;
        }

        if (maxInfoGain <= 0.0) return null;

        return attr;
    }

    public double infoGainForFeature(DataSet ds, int attrIdx) {

        Attribute attr = ds.getAttributes().get(attrIdx);
        Double infoGain = 0.0;

        // Iterate on each branch
        for (String branch : attr.getBranches()) {
            int pos = 0, neg = 0;
            // Iterate on each DI
            for (DataInstance di : ds.getDataInstances()) {
                // Filter each DI
                if(attr.satisfyBranch(branch, di.getAttrValueByIndex(attrIdx))) {
                    if (di.isOutputPositive()) {
                        pos++;
                    }
                    else {
                        neg++;
                    }
                }
            }

            int total = pos + neg;
            // System.out.println("Debug: name=" + attr.getName() + " total="+total+" size="+ds.getDataInstances().size()+" gain_for_branch:"+branch+ " = "+(-(pos*1.0/total)*log2(pos, total) - (neg*1.0/total)*log2(neg, total)));
            infoGain += (((1.0*total)/ds.getDataInstances().size())*(-(pos*1.0/total)*log2(pos, total) - (neg*1.0/total)*log2(neg, total)));
        }

        return infoGain;
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
