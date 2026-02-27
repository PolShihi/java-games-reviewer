package com.project.gamesreviewer.ui;

public class ConsoleUtils {

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }

    public static void printSubHeader(String title) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("  " + title);
        System.out.println("-".repeat(60));
    }

    public static void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void printSeparator() {
        System.out.println("-".repeat(60));
    }

    public static void pause() {
        System.out.println("\nНажмите Enter для продолжения...");
        try {
            System.in.read();
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public static void printTable(String[] headers, String[][] data) {
        int[] columnWidths = new int[headers.length];
        
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }
        
        for (String[] row : data) {
            for (int i = 0; i < row.length && i < columnWidths.length; i++) {
                if (row[i] != null && row[i].length() > columnWidths[i]) {
                    columnWidths[i] = row[i].length();
                }
            }
        }
        
        printTableRow(headers, columnWidths);
        printTableSeparator(columnWidths);
        
        for (String[] row : data) {
            printTableRow(row, columnWidths);
        }
    }

    private static void printTableRow(String[] cells, int[] widths) {
        for (int i = 0; i < cells.length; i++) {
            String cell = cells[i] != null ? cells[i] : "";
            System.out.printf("%-" + widths[i] + "s", cell);
            if (i < cells.length - 1) {
                System.out.print(" | ");
            }
        }
        System.out.println();
    }

    private static void printTableSeparator(int[] widths) {
        for (int i = 0; i < widths.length; i++) {
            System.out.print("-".repeat(widths[i]));
            if (i < widths.length - 1) {
                System.out.print("-+-");
            }
        }
        System.out.println();
    }

    public static String truncate(String str, int maxLength) {
        if (str == null) return "N/A";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}
