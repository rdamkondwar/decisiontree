package edu.wisc.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderHelper {

    public List<String> readData(String path) throws IOException {
        if (null == path || path.isEmpty()) {
            System.out.println("Invalid file path!");
            // return Collections.emptyList();
            throw new IllegalArgumentException("Invalid file path!");
        }

        File f = new File(path);
        List<String> result = new ArrayList<String>();


        BufferedReader br = new BufferedReader(new FileReader(f));

        String st;
        while ((st=br.readLine()) != null) {
            result.add(st);
        }

        return result;
    }
}
