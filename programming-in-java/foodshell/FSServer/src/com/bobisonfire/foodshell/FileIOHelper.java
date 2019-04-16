package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.entity.CSVSerializable;
import com.bobisonfire.foodshell.transformer.CSVObject;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.io.*;
import java.util.*;

/**
 * Класс, организовывающий чтение и запись коллекций в файлы в CSV-формате.
 */
public class FileIOHelper {
    /**
     * Записывает коллекцию в виде TreeMap (потому что по заданию в этом формате хранятся коллекции)
     * в файл формата CSV.
     * @param list Коллекция с однозначным соответствием строкового ключа и объекта коллекции.
     * @param <T> Тип объектов, содержащихся в коллекции.
     */
    public <T extends CSVSerializable> void writeCSVListIntoFile(List<T> list, String path) {
        if (list.size() == 0)
            return;

        T instance = list.get(0);
        File file = new File(path);

        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter writer = new PrintWriter(fileWriter, true);

            writer.write(instance.getCSVHead() + "\n");

            for (T elem: list) {
                writer.write(elem.toCSV() + "\n");
            }

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
    public List<ObjectTransformer> readCSVListFromFile(String path) {
        File file = new File(path);

        try {
            FileReader reader = new FileReader(file);
            Scanner scanner = new Scanner(reader);

            if (!scanner.hasNextLine())
                throw new IOException();

            List<ObjectTransformer> list = new ArrayList<>();
            String keys = scanner.nextLine();

            while (scanner.hasNextLine()) {
                ObjectTransformer csv = new CSVObject(keys, scanner.nextLine());
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
