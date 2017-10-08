package edu.wisc.ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DataInstance {
    List<Object> values;
    Object outputVal;

    public DataInstance(String line, List<Attribute> attributes, Attribute output) {
        values = new ArrayList<Object>(attributes.size());
        String [] splits = line.split(",");
        // System.out.println("hello world!"+Arrays.toString(splits));
        // System.out.println("hello world!"+Arrays.toString(attributes.toArray()));
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            String fVal = splits[i];
            values.add(parseValue(attr, fVal));
        }

        this.outputVal = parseValue(output, splits[attributes.size()]);
    }

    private Object parseValue(Attribute attr, String val) {
        if (attr instanceof NumericAttribute) {
            return Double.parseDouble(val.trim());
        }
        else if (attr instanceof NominalAttribute) {
            //TODO: Validate value of Enum
            return val.trim();
        }
        else if(attr instanceof OutputClass) {
            return val.trim();
        }
        else {
            //Impossible case.
            return null;
        }
    }

    public boolean isOutputPositive() {
        if(this.outputVal.equals("positive")) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "DataInstance{" +
                "values=" + Arrays.toString(values.toArray()) +
                ", outputVal=" + outputVal +
                '}';
    }
}
