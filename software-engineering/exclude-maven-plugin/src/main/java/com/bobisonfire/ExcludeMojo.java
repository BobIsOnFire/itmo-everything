package com.bobisonfire;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mojo(name = "exclude")
public class ExcludeMojo extends AbstractMojo {

    @Parameter(defaultValue = "exclude.cfg")
    private String configPath;

    @Parameter
    private List<String> searchDirectories;

    @Parameter(defaultValue = "[REDACTED]")
    private String replaceWord;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            List<String> excludeWords = Files.readAllLines(Paths.get(configPath));
            List<Path> searchPaths = getAllFilePaths(searchDirectories);
            replaceAllFiles(searchPaths, excludeWords, replaceWord);
        } catch (IOException e) {
            throw new MojoExecutionException("IO failure", e);
        }
    }

    private List<Path> getAllFilePaths(List<String> searchDirectories) throws IOException {
        List<Path> filePaths = new ArrayList<>();

        for (String path : searchDirectories) {
            filePaths.addAll(
                    Files.walk(Paths.get(path))
                            .filter(Files::isRegularFile)
                            .collect(Collectors.toList())
            );
        }

        return filePaths;
    }

    private void replaceAllFiles(List<Path> filePaths, List<String> searchWords, String replaceWord) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        for (Path path : filePaths) {
            String fromFile = new String(Files.readAllBytes(path), charset);
            for (String word : searchWords) fromFile = fromFile.replaceAll(word, replaceWord);
            Files.write(path, fromFile.getBytes(charset));
        }
    }
}
