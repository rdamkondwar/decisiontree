package edu.wisc.ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Attribute implements Cloneable {
    String name;

    List<String> branches = new ArrayList<String>();

    public Attribute(String name) {
        this.name = name.replaceAll("'", "");
    }

    public abstract void createBranches();

    public abstract boolean satisfyBranch(String branch, Object value);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                '}';
    }
}

class OutputClass extends Attribute {
    String pos, neg;
    public OutputClass(String name, String valueString) {
        super(name);
        String[] nominalValuesArr = valueString.replace("{", "").replace("}", "").split(",");
        this.neg = nominalValuesArr[0].trim();
        this.pos = nominalValuesArr[1].trim();
    }

    @Override
    public void createBranches() {
    }

    @Override
    public boolean satisfyBranch(String branch, Object value) {
        return false;
    }

    @Override
    public String toString() {
        return "OutputClass{" +
                "name='" + name + '\'' +
                ", pos='" + pos + '\'' +
                ", neg='" + neg + '\'' +
                '}';
    }
}

class NominalAttribute extends Attribute {
    List<String> values;

    public NominalAttribute(String name, List<String> values) {
        super(name);
        this.values = values;
    }

    public NominalAttribute(String name, String valueString) {
        super(name);
        String[] nominalValuesArr = valueString.replace("{", "").replace("}", "").split(",");
        this.values = new ArrayList<String>(nominalValuesArr.length);
        for(String val : nominalValuesArr) {
            this.values.add(val.trim());
        }
    }

    @Override
    public boolean satisfyBranch(String branch, Object value) {
        return branch.equals(value);
    }

    @Override
    public void createBranches() {
        for(String v : values) {
            this.branches.add(this.name+" = "+v);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        List<String> newValues = new ArrayList<String>();
        for(String v : this.values) {
            newValues.add(v);
        }
        return new NominalAttribute(this.name, newValues);
    }

    @Override
    public String toString() {
        return "NominalAttribute{" +
                "name='" + name + '\'' +
                ", values=" + Arrays.toString(values.toArray()) +
                '}';
    }
}

class NumericAttribute extends Attribute {
    Double threshold;
    String dataType;

    public NumericAttribute(String name, String dataType) {
        super(name);
        this.dataType = dataType.trim();
    }

    @Override
    public boolean satisfyBranch(String branch, Object value) {
        if (branch.contains(" <= ")) {
            if ((Double) value <= threshold) {
                return true;
            }
        }
        else {
            if ((Double) value > threshold) {
                // System.out.println("Debug: branch="+branch+ " value="+value);
                return true;
            }
        }
        return false;
    }

    @Override
    public void createBranches() {
        this.branches.add(this.name + " <= " + threshold);
        this.branches.add(this.name + " > " + threshold);
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new NumericAttribute(this.name, this.dataType);
    }

    @Override
    public String toString() {
        return "NumericAttribute{" +
                "name='" + name + '\'' +
                ", threshold=" + threshold +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
