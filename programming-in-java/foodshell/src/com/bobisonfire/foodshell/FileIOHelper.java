package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.entity.CSVSerializable;
import com.bobisonfire.foodshell.transformer.CSVObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class FileIOHelper {
    public <T extends CSVSerializable> void writeCSVMapIntoFile(TreeMap<String, T> map, boolean append) {
        if (map.size() == 0)
            return;

        T instance = map.firstEntry().getValue();
        File file = new File(instance.getPath());

        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, append);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            if (!append)
                writer.write(instance.getCSVHead() + "\n");

            for (Map.Entry<String, T> elem: map.entrySet()) {
                writer.write(elem.getValue().toCSV() + "\n");
            }

            writer.flush();
            writer.close();
        }
        catch (IOException exc) {
            System.out.println( "Cannot write file " + file.getAbsolutePath() );
        }
    }

    public ArrayList<CSVObject> readCSVListFromFile(String path) {
        File file = new File(path);

        try {
            FileReader reader = new FileReader(file);
            Scanner scanner = new Scanner(reader);

            if (!scanner.hasNextLine())
                throw new IOException();

            ArrayList<CSVObject> list = new ArrayList<>();
            String keys = scanner.nextLine();

            while (scanner.hasNextLine()) {
                CSVObject csv = new CSVObject(keys, scanner.nextLine());
                list.add( csv );
            }

            return list;
        }
        catch (IOException exc) {
            if (file.exists())
                System.out.println( "Cannot read file " + file.getAbsolutePath() );

            return new ArrayList<>();
        }
    }
}
