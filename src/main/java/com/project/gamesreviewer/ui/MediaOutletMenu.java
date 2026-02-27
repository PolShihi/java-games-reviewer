package com.project.gamesreviewer.ui;

import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.model.MediaOutlet;
import com.project.gamesreviewer.service.MediaOutletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class MediaOutletMenu {

    @Autowired
    private MediaOutletService mediaOutletService;

    private final Scanner scanner;
    private final InputValidator validator;

    public MediaOutletMenu() {
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator(scanner);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("УПРАВЛЕНИЕ МЕДИА-ИЗДАНИЯМИ");

            System.out.println("1. Показать все издания");
            System.out.println("2. Найти издание по ID");
            System.out.println("3. Добавить новое издание");
            System.out.println("4. Редактировать издание");
            System.out.println("5. Удалить издание");
            System.out.println("0. Назад в главное меню");

            int choice = validator.readInt("\nВаш выбор: ", 0, 5);

            switch (choice) {
                case 1 -> showAllMediaOutlets();
                case 2 -> findMediaOutletById();
                case 3 -> createMediaOutlet();
                case 4 -> updateMediaOutlet();
                case 5 -> deleteMediaOutlet();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void showAllMediaOutlets() {
        ConsoleUtils.printSubHeader("Список всех медиа-изданий");

        List<MediaOutlet> outlets = mediaOutletService.getAllMediaOutlets();

        if (outlets.isEmpty()) {
            ConsoleUtils.printWarning("Медиа-издания не найдены.");
        } else {
            String[] headers = {"ID", "Название", "Веб-сайт", "Год основания"};
            String[][] data = new String[outlets.size()][4];

            for (int i = 0; i < outlets.size(); i++) {
                MediaOutlet outlet = outlets.get(i);
                data[i][0] = String.valueOf(outlet.id());
                data[i][1] = ConsoleUtils.truncate(outlet.name(), 30);
                data[i][2] = ConsoleUtils.truncate(outlet.websiteUrl(), 30);
                data[i][3] = outlet.foundedYear() != null ? String.valueOf(outlet.foundedYear()) : "N/A";
            }

            ConsoleUtils.printTable(headers, data);
            ConsoleUtils.printInfo("Всего изданий: " + outlets.size());
        }

        ConsoleUtils.pause();
    }

    private void findMediaOutletById() {
        ConsoleUtils.printSubHeader("Поиск издания по ID");

        int outletId = validator.readInt("Введите ID издания: ");
        MediaOutlet outlet = mediaOutletService.getMediaOutletById(outletId);

        if (outlet == null) {
            ConsoleUtils.printError("Издание с ID " + outletId + " не найдено.");
        } else {
            System.out.println();
            ConsoleUtils.printSeparator();
            System.out.println("ID: " + outlet.id());
            System.out.println("Название: " + outlet.name());
            if (outlet.websiteUrl() != null && !outlet.websiteUrl().isEmpty()) {
                System.out.println("Веб-сайт: " + outlet.websiteUrl());
            }
            if (outlet.foundedYear() != null) {
                System.out.println("Год основания: " + outlet.foundedYear());
            }
            ConsoleUtils.printSeparator();
        }

        ConsoleUtils.pause();
    }

    private void createMediaOutlet() {
        ConsoleUtils.printSubHeader("Добавление нового издания");

        String name = validator.readNonEmptyString("Название издания: ");
        String websiteUrl = validator.readStringOrNull("Веб-сайт (Enter для пропуска): ");
        Integer foundedYear = validator.readYearFoundedOrNull("Год основания (Enter для пропуска): ");

        MediaOutlet newOutlet = new MediaOutlet(0, name, websiteUrl, foundedYear);
        
        try {
            int outletId = mediaOutletService.createMediaOutlet(newOutlet);
            ConsoleUtils.printSuccess("Издание успешно создано с ID: " + outletId);
        } catch (DuplicateEntryException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void updateMediaOutlet() {
        ConsoleUtils.printSubHeader("Редактирование издания");

        int outletId = validator.readInt("Введите ID издания: ");
        MediaOutlet outlet = mediaOutletService.getMediaOutletById(outletId);

        if (outlet == null) {
            ConsoleUtils.printError("Издание с ID " + outletId + " не найдено.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nТекущие данные:");
        System.out.println("Название: " + outlet.name());
        System.out.println("Веб-сайт: " + (outlet.websiteUrl() != null ? outlet.websiteUrl() : "Нет"));
        System.out.println("Год основания: " + (outlet.foundedYear() != null ? outlet.foundedYear() : "Нет"));

        System.out.println("\nОставьте поле пустым, чтобы не изменять значение.");

        String newName = validator.readString("Новое название: ");
        if (newName.isEmpty()) newName = outlet.name();

        String newWebsite = validator.readString("Новый веб-сайт: ");
        if (newWebsite.isEmpty()) newWebsite = outlet.websiteUrl();

        Integer newYear = validator.readYearFoundedOrNull("Новый год основания (Enter для пропуска): ");
        if (newYear == null) newYear = outlet.foundedYear();

        MediaOutlet updatedOutlet = new MediaOutlet(outletId, newName, newWebsite, newYear);
        mediaOutletService.updateMediaOutlet(updatedOutlet);

        ConsoleUtils.printSuccess("Издание успешно обновлено!");
        ConsoleUtils.pause();
    }

    private void deleteMediaOutlet() {
        ConsoleUtils.printSubHeader("Удаление издания");

        int outletId = validator.readInt("Введите ID издания для удаления: ");
        MediaOutlet outlet = mediaOutletService.getMediaOutletById(outletId);

        if (outlet == null) {
            ConsoleUtils.printError("Издание с ID " + outletId + " не найдено.");
        } else {
            System.out.println("\nВы собираетесь удалить издание: " + outlet.name());
            if (validator.readYesNo("Подтвердите удаление")) {
                mediaOutletService.deleteMediaOutlet(outletId);
                ConsoleUtils.printSuccess("Издание успешно удалено!");
            } else {
                ConsoleUtils.printInfo("Удаление отменено.");
            }
        }

        ConsoleUtils.pause();
    }
}
