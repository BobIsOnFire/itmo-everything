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
     * @param set Коллекция с однозначным соответствием строкового ключа и объекта коллекции.
     * @param <T> Тип объектов, содержащихся в коллекции.
     */
    public <T extends CSVSerializable> void writeCSVSetIntoFile(Set<T> set, String path) {
        Iterator<T> iterator = set.iterator();
        if (!iterator.hasNext())
            return;

        T instance = iterator.next();
        File file = new File(path);

        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter writer = new PrintWriter(fileWriter, true);

            writer.write(instance.getCSVHead() + "\n");
            set.forEach(elem -> writer.write(elem.toCSV() + "\n"));
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
    public Set<ObjectTransformer> readCSVSetFromFile(String path) {
        File file = new File(path);

        try {
            FileReader reader = new FileReader(file);
            Scanner scanner = new Scanner(reader);

            if (!scanner.hasNextLine())
                throw new IOException();

            Set<ObjectTransformer> set = new HashSet<>();
            String keys = scanner.nextLine();

            while (scanner.hasNextLine()) {
                ObjectTransformer csv = new CSVObject(keys, scanner.nextLine());
                set.add( csv );
            }

            return set;
        }
        catch (IOException exc) {
            if (file.exists())
                System.out.println( "Cannot read file " + file.getAbsolutePath() );

            return new HashSet<>();
        }
    }
}
