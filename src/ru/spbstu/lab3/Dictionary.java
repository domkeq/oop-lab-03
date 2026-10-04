package ru.spbstu.lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    // ключи хранятся в нижнем регистре, слова через один пробел
    private final Map<String, String> translations = new HashMap<>();
    // самая длинная левая часть в словах
    private int maxPhraseLength;

    public Dictionary(String fileName) throws FileReadException, InvalidFileFormatException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.startsWith("﻿")) {
                    line = line.substring(1);
                }
                if (!line.isBlank()) {
                    addLine(line, lineNumber);
                }
            }
        } catch (NoSuchFileException e) {
            throw new FileReadException("файл " + fileName + " не найден", e);
        } catch (IOException | InvalidPathException e) {
            throw new FileReadException("не удалось прочитать файл " + fileName, e);
        }
    }

    private void addLine(String line, int lineNumber) throws InvalidFileFormatException {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 2) {
            throw new InvalidFileFormatException("строка " + lineNumber + ": ожидается \"слово или выражение | перевод\"");
        }
        String phrase = normalize(parts[0]);
        String translation = parts[1].trim();
        if (phrase.isEmpty() || translation.isEmpty()) {
            throw new InvalidFileFormatException("строка " + lineNumber + ": пустое слово или перевод");
        }
        translations.put(phrase, translation);
        maxPhraseLength = Math.max(maxPhraseLength, phrase.split(" ").length);
    }

    private static String normalize(String phrase) {
        return phrase.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    public String find(String phrase) {
        return translations.get(normalize(phrase));
    }

    public int getMaxPhraseLength() {
        return maxPhraseLength;
    }
}
