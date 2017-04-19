package com.covain.projects.emailer.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MailParser {

    private static String parseFile(String inputPath, String outputPath) {
        String result = null;
        try (FileWriter fileWriter = new FileWriter(outputPath)) {
            List<String> lines = Files.readAllLines(Paths.get(inputPath), Charset.forName("UTF-8"));
            StringBuilder stringBuilder = new StringBuilder(lines.size() * 3);
            lines.stream()
                    .map((line) -> line.split("[ \n]+"))
                    .forEach((words) -> Arrays.stream(words)
                            .filter((word) -> Pattern.compile("\\S+@\\S+").matcher(word).find())
                            .forEach((email) -> stringBuilder.append(email + "\n")));

            result = stringBuilder.toString();
            fileWriter.write(result);
            fileWriter.close();
        } catch (IOException e) {

        }
        return result;
    }

    public static List<String> parseString(String input) {
        List<String> result = new ArrayList<>();
        Arrays.stream(input.split("[ \\n\\t]+"))
                .filter((word) -> Pattern.compile("\\S+@\\S+").matcher(word).find())
                .forEach(result::add);
        return result;
    }
}

