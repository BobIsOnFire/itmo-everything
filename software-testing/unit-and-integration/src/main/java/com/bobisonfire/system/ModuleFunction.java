package com.bobisonfire.system;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface ModuleFunction {
    default void saveValues(Path path, Point[] values) throws IOException {
        byte[] bytes = Arrays.stream(values)
                .map(Point::toString)
                .collect(Collectors.joining("\n"))
                .getBytes(StandardCharsets.UTF_8);

        Files.write(path, bytes);
    }
}
