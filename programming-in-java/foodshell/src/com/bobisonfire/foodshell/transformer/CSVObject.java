package com.bobisonfire.foodshell.transformer;

public class CSVObject extends ObjectTransformer {
    public CSVObject(String keys, String fields) {
        value = fields;
        String[] keyArray = keys.split(",");
        String[] fieldArray = fields.split(",");

        for (int i = 0; i < keyArray.length; i++) {
            values.put(keyArray[i], fieldArray[i]);
        }
    }
}
