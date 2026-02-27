package com.project.gamesreviewer.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MainMenu {

    @Autowired
    private GameMenu gameMenu;

    @Autowired
    private CompanyMenu companyMenu;

    @Autowired
    private GenreMenu genreMenu;

    @Autowired
    private MediaOutletMenu mediaOutletMenu;

    @Autowired
    private ReviewMenu reviewMenu;

    @Autowired
    private SystemRequirementMenu systemRequirementMenu;

    private final Scanner scanner;
    private final InputValidator validator;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator(scanner);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("GAMES REVIEWER - Управление базой данных игр");

            System.out.println("\nОсновное меню:\n");
            System.out.println("1. Управление играми");
            System.out.println("2. Управление компаниями");
            System.out.println("3. Управление жанрами");
            System.out.println("4. Управление медиа-изданиями");
            System.out.println("5. Управление обзорами");
            System.out.println("6. Управление системными требованиями");
            System.out.println("0. Выход");

            int choice = validator.readInt("\nВаш выбор: ", 0, 6);

            switch (choice) {
                case 1 -> gameMenu.show();
                case 2 -> companyMenu.show();
                case 3 -> genreMenu.show();
                case 4 -> mediaOutletMenu.show();
                case 5 -> reviewMenu.show();
                case 6 -> systemRequirementMenu.show();
                case 0 -> {
                    ConsoleUtils.printInfo("Спасибо за использование приложения!");
                    return;
                }
            }
        }
    }
}
