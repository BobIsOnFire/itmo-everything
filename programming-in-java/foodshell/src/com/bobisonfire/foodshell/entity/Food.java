package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс, реализующий приемы пищи. Пища характеризуется уровнем насыщения и временем действия.<br>
 * Тут, в принципе, говорить больше нечего.
 */
public class Food implements CSVSerializable {
    public static final String CSV_HEAD = "consumer,name,saturation,saturationTime,eatingDate";
    public static String PATH = "meals.csv";

    public String getCSVHead() {
        return CSV_HEAD;
    }

    public String getPath() {
        return PATH;
    }

    public String toCSV() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return String.format("%s,%s,%d,%d,%s",
                consumer.getName(), name, saturation, saturationTime, sdf.format(eatingDate));
    }



    private Human consumer;
    private String name;
    private int saturation;
    private long saturationTime;
    private Date eatingDate;

    public Food(Human consumer, String name, int saturation, long saturationTime) {
        this.consumer = consumer;
        this.name = name;
        this.saturation = saturation;
        this.saturationTime = saturationTime;
        this.eatingDate = new Date();
    }

    public Food(ObjectTransformer objectTransformer) {
        consumer = Human.getHumanByName( objectTransformer.getString("consumer") );
        name = objectTransformer.getString("name");
        saturation = objectTransformer.getInt("saturation");
        saturationTime = objectTransformer.getLong("saturationTime");

        eatingDate = objectTransformer.getDate("eatingDate", "dd.MM.yyyy");
    }

    public Human getConsumer() {
        return consumer;
    }

    public String getName() {
        return name;
    }

    public int getSaturation() {
        return saturation;
    }

    public long getSaturationTime() {
        return saturationTime;
    }

    public Date getSaturationExpirationDate() {
        return new Date(eatingDate.getTime() + saturationTime);
    }

    public boolean isAffecting() {
        return new Date().before( getSaturationExpirationDate() );
    }
}
