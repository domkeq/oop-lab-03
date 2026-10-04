package ru.spbstu.lab3;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Translator {
    // слово: буквы, цифры, апостроф и дефис внутри
    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}]+(['-][\\p{L}\\p{N}]+)*");

    private final Dictionary dictionary;

    public Translator(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String translate(String text) {
        List<MatchResult> words = WORD.matcher(text).results().toList();
        StringBuilder result = new StringBuilder();
        int position = 0;

        int i = 0;
        while (i < words.size()) {
            // пробелы и знаки препинания перед словом переносим как есть
            result.append(text, position, words.get(i).start());

            // сначала пробуем самую длинную фразу, потом всё короче
            String translation = null;
            int length = Math.min(dictionary.getMaxPhraseLength(), words.size() - i);
            for (; length > 0; length--) {
                String phrase = joinWords(text, words, i, length);
                if (phrase != null) {
                    translation = dictionary.find(phrase);
                    if (translation != null) {
                        break;
                    }
                }
            }

            if (translation == null) {
                result.append(words.get(i).group());
                length = 1;
            } else {
                result.append(translation);
            }
            position = words.get(i + length - 1).end();
            i += length;
        }
        result.append(text.substring(position));
        return result.toString();
    }

    // склеивает length слов начиная с from, null если между ними есть знаки препинания
    private static String joinWords(String text, List<MatchResult> words, int from, int length) {
        StringBuilder phrase = new StringBuilder(words.get(from).group());
        for (int j = from + 1; j < from + length; j++) {
            String between = text.substring(words.get(j - 1).end(), words.get(j).start());
            if (!between.isBlank()) {
                return null;
            }
            phrase.append(' ').append(words.get(j).group());
        }
        return phrase.toString();
    }
}
