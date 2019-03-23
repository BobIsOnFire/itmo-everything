package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.entity.CSVSerializable;
import com.bobisonfire.foodshell.transformer.CSVObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Класс, организовывающий чтение и запись коллекций в файлы в CSV-формате.
 */
public class FileIOHelper {
    /**
     * Записывает коллекцию в виде TreeMap (потому что по заданию в этом формате хранятся коллекции)
     * в файл формата CSV.
     * @param map Коллекция с однозначным соответствием строкового ключа и объекта коллекции.
     * @param append Флаг, соответствующий добавлению информацию в конец файла (true) или его полной перезаписи (false).
     * @param <T> Тип объектов, содержащихся в коллекции.
     */
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

    /**
     * Читает список CSV-объектов из файла по заданному пути.
     * @param path Путь до файла формата CSV.
     * @return Список CSV-объектов, где соответствие полей и значений формируется по шапке файла.
     */
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
