package com.bobisonfire.foodshell.transformer;

import com.bobisonfire.foodshell.exc.TransformerException;

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

    private boolean isJSON(String string) { // after deleting spaces and tabs
        return string.matches("^\\{(.*)}$");
    }

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
