package edu.wisc.ml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DataSet {

    private String relationName;
    private List<Attribute> attributes;
    private Attribute output;
    private List<DataInstance> dataInstances;

    public DataSet() {
        attributes = new ArrayList<Attribute>();
        dataInstances = new ArrayList<DataInstance>();
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

    Attribute processAttribute(String name, String dataType) {
        if (dataType.equals("real") || dataType.equals("numeric")) {
            return new NumericAttribute(name, dataType);
        }
        else if (name.equals("'class'")) {
            // System.out.println("class");
            return new OutputClass(name, dataType);
        }
        else if (dataType.endsWith("}") && dataType.startsWith("{")) {
            return new NominalAttribute(name, dataType);
        }
        else {
            // Ignore for now.
            return null;
        }
    }

    public void addData(List<String> data) {
        Iterator<String> iter = data.iterator();

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
                Attribute attr = processAttribute(splits[1], splits[2]);
                // System.out.println("Debug: "+splits[1]+" "+splits[2]);
                if (!(attr instanceof OutputClass)) {
                    this.attributes.add(attr);
                }
                else{
                    this.output = attr;
                }
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
        System.out.println(dataInstances.size());
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
