package edu.wisc.ml;

import java.util.*;


public class DataSet {

    private String relationName;
    private List<Attribute> attributes;
    private Attribute output;
    private List<DataInstance> dataInstances;

    public DataSet() {
        attributes = new ArrayList<Attribute>();
        dataInstances = new ArrayList<DataInstance>();
    }

    public void addDataInstance(DataInstance di) {
        this.dataInstances.add(di);
    }

    void processData(List<String> data) {
        // System.out.println("Debug: in processData: "+data.size());
        for(String line : data) {
            // System.out.println(line);
            DataInstance di = new DataInstance(line, attributes, output);
            this.dataInstances.add(di);
            // System.out.println(di);
        }

    }

    Attribute processAttribute(String name, int idx, String dataType) {
        if (dataType.equals("real") || dataType.equals("numeric")) {
            return new NumericAttribute(name, idx, dataType);
        }
        else if (name.equals("'class'")) {
            // System.out.println("class");
            return new OutputClass(name, idx, dataType);
        }
        else if (dataType.endsWith("}") && dataType.startsWith("{")) {
            return new NominalAttribute(name, idx, dataType);
        }
        else {
            // Ignore for now.
            return null;
        }
    }

    public void addData(List<String> data) {
        Iterator<String> iter = data.iterator();

        int idx = 0;

        while(iter.hasNext()) {
            String line = iter.next();

            if (line.startsWith("%")) {
                // Ignore comment.
            }
            else if (line.startsWith("@relation")) {
                // Add relation name
                String [] splits = line.split(" ", 3);
                this.relationName = splits[1];
            }
            else if (line.startsWith("@attribute")) {
                String [] splits = line.split(" ", 3);
                Attribute attr = processAttribute(splits[1], idx, splits[2]);
                // System.out.println("Debug: "+splits[1]+" "+splits[2]);
                if (!(attr instanceof OutputClass)) {
                    this.attributes.add(attr);
                }
                else{
                    this.output = attr;
                }
                idx++;
            }
            else if(line.startsWith("@data")) {
                iter.remove();
                break;
            }

            iter.remove();
        }

        // System.out.println("Adding data instances");
        // System.out.println("Size = "+data.size());
//        for(Attribute attr: this.attributes) {
//            System.out.println(attr);
//        }
        // System.out.println("output = "+this.output);

        processData(data);
        // System.out.println(dataInstances.size());
        // calculateThresholds();
        // createBranches();
    }

    public void calculateThresholds() {
        int index = 0;
        for (Attribute attr : this.attributes) {
            if(attr instanceof NumericAttribute) {
                // System.out.println("index = "+index+ " attr="+attr);
                ((NumericAttribute) attr).setCandidateThresholds(calculateThresholdForIndex(attr.index));
            }
            // System.out.println("Attr = "+attr + " candidates="+((NumericAttribute)attr).getCandidateThresholds());
            index++;
        }
    }

    public void createBranches() {
        for (Attribute attr : this.attributes) {
            if(attr instanceof NominalAttribute) {
                attr.createBranches();
            }
        }
    }

    private class Tuple {
        int pos = 0, neg = 0;
        Tuple(int n, int p) {
            this.neg = n;
            this.pos = p;
        }
    }

    private List<Double> calculateThresholdForIndex(int idx) {
        Map<Double, Tuple> data = new TreeMap<Double, Tuple>();
        // int totalPos = 0, totalNeg = 0;

        for (DataInstance di : this.dataInstances) {
            if (di.getAttrValueByIndex(idx) instanceof String) {
                System.out.println("Wrong !" + di.getAttrValueByIndex(idx) + " di="+di);
            }
            Double fValue = (Double)di.getAttrValueByIndex(idx);
            String opValue = (String)di.outputVal;
            Tuple t = data.get(fValue);

            /*if(opValue.equals("positive")) {
                totalPos++;
            }
            else {
                totalNeg++;
            }*/

            if (null == t) {
                if (opValue.equals("positive")) {
                    data.put(fValue, new Tuple(0,1));
                }
                else {
                    data.put(fValue, new Tuple(1, 0));
                }
            }
            else {
                if (opValue.equals("positive")) {
                    t.pos++;
                }
                else {
                    t.neg++;
                }
                data.put(fValue, t);
            }
        }

        List<Double> thresholds = new ArrayList<Double>();

        // Map<Double, Tuple> thresholdMap = new TreeMap<Double, Tuple>();
        Map.Entry prevEntry = null;
        for (Map.Entry entry : data.entrySet()) {
            // System.out.println("Debug: "+entry.getKey());
            if (prevEntry != null) {
                Tuple t1 = (Tuple) prevEntry.getValue();
                Tuple t2 = (Tuple) entry.getValue();

                if (t2.neg == t1.neg && t1.neg == 0) {
                    // Do nothing.
                } else if (t2.pos == t1.pos && t1.pos == 0) {
                    // Do nothing.
                } else {
                    // System.out.println("attr = " + idx +"tr = "+((Double)entry.getKey() + (Double)prevEntry.getKey())/2.0);
                    thresholds.add(((Double) entry.getKey() + (Double) prevEntry.getKey()) / 2.0);
                    // thresholdMap.put(((Double)entry.getKey() + (Double)prevEntry.getKey())/2.0, t1);
                }
            }
            prevEntry = entry;
        }

        // if (thresholds.size() == 0) System.out.println("Debug: size = 0"+thresholds);
        // System.out.println("Debug: "+thresholds);
        return thresholds;

        /*Double bestThreshold = 0.0;
        Double minEntropy = Double.MAX_VALUE;

        // Choose best threshold.
        for (Double tr : thresholds) {
            System.out.println("Debug = "+tr);
            Tuple t = data.get(tr);
            int total = t.neg + t.pos;

            // Less than equal to branch
            Double entropy = ((1.0*total)/this.dataInstances.size())*(-(t.pos*1.0/total)*DecisionTree.log2(t.pos, total) - (t.neg*1.0/total)*DecisionTree.log2(t.neg, total));

            int pos2 = totalPos - t.pos;
            int neg2 = totalNeg - t.neg;
            int total2 = pos2 + neg2;

            // More than branch
            entropy += ((1.0*total2)/this.dataInstances.size())*(-(pos2*1.0/total2)*DecisionTree.log2(pos2, total2) - (neg2*1.0/total2)*DecisionTree.log2(neg2, total2));

            if (minEntropy > entropy) {
                minEntropy = entropy;
                bestThreshold = tr;
            }
        }*/


        // ((NumericAttribute)this.attributes.get(idx)).setThreshold(bestThreshold);
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Attribute getOutput() {
        return output;
    }

    public void setOutput(Attribute output) {
        this.output = output;
    }

    public List<DataInstance> getDataInstances() {
        return dataInstances;
    }

    public void setDataInstances(List<DataInstance> dataInstances) {
        this.dataInstances = dataInstances;
    }
}


