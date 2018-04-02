package com.screenripper.tests.webdriver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadTheFile {
    public static ArrayList<String> readTheFilelist(String fileName) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        String line;

        // FileReader reads text files in the default encoding.
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader =
                new BufferedReader(fileReader);

        while ((line = bufferedReader.readLine()) != null) {
            list.add(line);
        }
        return list;
    }
}
