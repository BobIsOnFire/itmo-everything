package com.bobisonfire.foodshell.transformer;

import com.bobisonfire.foodshell.exc.TransformerException;

/**
 * Класс, реализующий десериализацию простых JSON-объектов.
 */
public class JSONObject extends ObjectTransformer {
    public JSONObject(String object) {

        value = deleteWhitespacesNotQuoted(object);
        if (!isJSON(value))
            throw new TransformerException();

        String[] entries = value.split("[},{]");

        for (String entry: entries) {
            if (entry.equals(""))
                continue;

            String key;
            String value;

            int semicolon = indexOfNotQuoted(':', entry);
            String isQuotedRegex = "^\"(.*)\"$";
            if (semicolon == -1)
                throw new TransformerException();

            key = entry.substring(0, semicolon);
            if ( key.matches(isQuotedRegex) )
                key = key.substring(1, key.length() - 1);

            value = entry.substring(semicolon + 1);
            if ( value.matches(isQuotedRegex) )
                value = value.substring(1, value.length() - 1);

            values.put(key, value);

        }
    }

    /**
     * Проверяет соответствие строки простейшему формату JSON.
     * @param string Строка на проверку.
     */
    private boolean isJSON(String string) {
        return string.matches("^\\{(.*)}$");
    }

    /**
     * Удаляет не заключенные в кавычки пробелы (в т.ч. табы, переносы строки и т.д.).
     * @param string Исходная строка
     * @return Строка без whitespace'ов.
     */
    private String deleteWhitespacesNotQuoted(String string) {
        StringBuilder sb = new StringBuilder();
        boolean quote = false;
        for (char ch: string.toCharArray()) {
            if (ch == '"')
                quote = !quote;
            if (quote || !Character.isWhitespace(ch) )
                sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Модификация строкового метода indexOf, выполняющая поиск первого не заключенного в кавычки вхождения символа.
     * @param search Символ, который нужно найти.
     * @param string Строка, по которой осуществляется поиск.
     * @return Индекс первого вхождения.
     */
    private int indexOfNotQuoted(char search, String string) {
        boolean quote = false;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '"')
                quote = !quote;
            if (!quote && string.charAt(i) == search)
                return i;
        }
        return -1;
    }
}
