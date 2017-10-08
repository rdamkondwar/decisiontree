package edu.wisc.ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Attribute {
    String name;

    public Attribute(String name) {
        this.name = name.replaceAll("'", "");
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
        this.pos = nominalValuesArr[0].trim();
        this.neg = nominalValuesArr[1].trim();
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

    public NominalAttribute(String name, String valueString) {
        super(name);
        String[] nominalValuesArr = valueString.replace("{", "").replace("}", "").split(",");
        this.values = new ArrayList<String>(nominalValuesArr.length);
        for(String val : nominalValuesArr) {
            this.values.add(val.trim());
        }
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
    public String toString() {
        return "NumericAttribute{" +
                "name='" + name + '\'' +
                ", threshold=" + threshold +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
