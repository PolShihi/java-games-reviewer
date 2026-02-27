package com.project.gamesreviewer.ui;

import java.util.Scanner;

public class InputValidator {

    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("Некорректный ввод. Введите целое число.");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            ConsoleUtils.printError("Значение должно быть от " + min + " до " + max);
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("Некорректный ввод. Введите число.");
            }
        }
    }

    public double readDouble(String prompt, double min, double max) {
        while (true) {
            double value = readDouble(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            ConsoleUtils.printError("Значение должно быть от " + min + " до " + max);
        }
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            String value = readString(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            ConsoleUtils.printError("Поле не может быть пустым.");
        }
    }

    public String readStringOrNull(String prompt) {
        String value = readString(prompt);
        return value.isEmpty() ? null : value;
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readString(prompt + " (y/n): ").toLowerCase();
            if (value.equals("y") || value.equals("yes") || value.equals("д") || value.equals("да")) {
                return true;
            }
            if (value.equals("n") || value.equals("no") || value.equals("н") || value.equals("нет")) {
                return false;
            }
            ConsoleUtils.printError("Введите y (да) или n (нет).");
        }
    }

    public Integer readIntOrNull(String prompt) {
        String value = readString(prompt);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ConsoleUtils.printError("Некорректный ввод.");
            return readIntOrNull(prompt);
        }
    }

    public Double readDoubleOrNull(String prompt) {
        String value = readString(prompt);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            ConsoleUtils.printError("Некорректный ввод.");
            return readDoubleOrNull(prompt);
        }
    }

    public int readYearRelease(String prompt) {
        int minYear = 1950;
        
        while (true) {
            int year = readInt(prompt);
            if (year >= minYear) {
                return year;
            }
            ConsoleUtils.printError("Год релиза должен быть не менее " + minYear);
        }
    }

    public Integer readYearReleaseOrNull(String prompt) {
        String value = readString(prompt);
        if (value.isEmpty()) {
            return null;
        }
        
        int minYear = 1950;
        
        try {
            int year = Integer.parseInt(value);
            if (year >= minYear) {
                return year;
            }
            ConsoleUtils.printError("Год релиза должен быть не менее " + minYear);
            return readYearReleaseOrNull(prompt);
        } catch (NumberFormatException e) {
            ConsoleUtils.printError("Некорректный ввод.");
            return readYearReleaseOrNull(prompt);
        }
    }

    public int readYearFounded(String prompt) {
        int minYear = 1900;
        
        while (true) {
            int year = readInt(prompt);
            if (year >= minYear) {
                return year;
            }
            ConsoleUtils.printError("Год основания должен быть не менее " + minYear);
        }
    }

    public Integer readYearFoundedOrNull(String prompt) {
        String value = readString(prompt);
        if (value.isEmpty()) {
            return null;
        }
        
        int minYear = 1900;
        
        try {
            int year = Integer.parseInt(value);
            if (year >= minYear) {
                return year;
            }
            ConsoleUtils.printError("Год основания должен быть не менее " + minYear);
            return readYearFoundedOrNull(prompt);
        } catch (NumberFormatException e) {
            ConsoleUtils.printError("Некорректный ввод.");
            return readYearFoundedOrNull(prompt);
        }
    }
}
