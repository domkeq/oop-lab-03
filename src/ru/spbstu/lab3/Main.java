package ru.spbstu.lab3;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String fileName = args.length > 0 ? args[0] : "dictionary.txt";

        try {
            Translator translator = new Translator(new Dictionary(fileName));
            System.out.println("Словарь " + fileName + " загружен");

            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Введите текст для перевода (пустая строка для выхода):");
                while (scanner.hasNextLine()) {
                    String text = scanner.nextLine();
                    if (text.isBlank()) {
                        break;
                    }
                    System.out.println(translator.translate(text));
                }
            }
        } catch (FileReadException e) {
            System.out.println("Ошибка чтения словаря: " + e.getMessage());
        } catch (InvalidFileFormatException e) {
            System.out.println("Неверный формат словаря, " + e.getMessage());
        }
    }
}
