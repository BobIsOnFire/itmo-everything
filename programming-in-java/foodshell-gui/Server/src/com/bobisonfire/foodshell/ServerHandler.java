package com.bobisonfire.foodshell;

import java.io.IOException;

public interface ServerHandler<T> {
    void accept(T object) throws IOException;
}
