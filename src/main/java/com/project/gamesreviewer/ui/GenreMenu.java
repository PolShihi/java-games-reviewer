package com.project.gamesreviewer.ui;

import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.model.Genre;
import com.project.gamesreviewer.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class GenreMenu {

    @Autowired
    private GenreService genreService;

    private final Scanner scanner;
    private final InputValidator validator;

    public GenreMenu() {
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator(scanner);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("УПРАВЛЕНИЕ ЖАНРАМИ");

            System.out.println("1. Показать все жанры");
            System.out.println("2. Найти жанр по ID");
            System.out.println("3. Добавить новый жанр");
            System.out.println("4. Редактировать жанр");
            System.out.println("5. Удалить жанр");
            System.out.println("0. Назад в главное меню");

            int choice = validator.readInt("\nВаш выбор: ", 0, 5);

            switch (choice) {
                case 1 -> showAllGenres();
                case 2 -> findGenreById();
                case 3 -> createGenre();
                case 4 -> updateGenre();
                case 5 -> deleteGenre();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void showAllGenres() {
        ConsoleUtils.printSubHeader("Список всех жанров");

        List<Genre> genres = genreService.getAllGenres();

        if (genres.isEmpty()) {
            ConsoleUtils.printWarning("Жанры не найдены.");
        } else {
            String[] headers = {"ID", "Название"};
            String[][] data = new String[genres.size()][2];

            for (int i = 0; i < genres.size(); i++) {
                Genre genre = genres.get(i);
                data[i][0] = String.valueOf(genre.id());
                data[i][1] = genre.name();
            }

            ConsoleUtils.printTable(headers, data);
            ConsoleUtils.printInfo("Всего жанров: " + genres.size());
        }

        ConsoleUtils.pause();
    }

    private void findGenreById() {
        ConsoleUtils.printSubHeader("Поиск жанра по ID");

        int genreId = validator.readInt("Введите ID жанра: ");
        Genre genre = genreService.getGenreById(genreId);

        if (genre == null) {
            ConsoleUtils.printError("Жанр с ID " + genreId + " не найден.");
        } else {
            System.out.println();
            ConsoleUtils.printSeparator();
            System.out.println("ID: " + genre.id());
            System.out.println("Название: " + genre.name());
            ConsoleUtils.printSeparator();
        }

        ConsoleUtils.pause();
    }

    private void createGenre() {
        ConsoleUtils.printSubHeader("Добавление нового жанра");

        String name = validator.readNonEmptyString("Название жанра: ");

        Genre newGenre = new Genre(0, name);
        
        try {
            int genreId = genreService.createGenre(newGenre);
            ConsoleUtils.printSuccess("Жанр успешно создан с ID: " + genreId);
        } catch (DuplicateEntryException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void updateGenre() {
        ConsoleUtils.printSubHeader("Редактирование жанра");

        int genreId = validator.readInt("Введите ID жанра: ");
        Genre genre = genreService.getGenreById(genreId);

        if (genre == null) {
            ConsoleUtils.printError("Жанр с ID " + genreId + " не найден.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nТекущие данные:");
        System.out.println("Название: " + genre.name());

        System.out.println("\nОставьте поле пустым, чтобы не изменять значение.");

        String newName = validator.readString("Новое название: ");
        if (newName.isEmpty()) newName = genre.name();

        Genre updatedGenre = new Genre(genreId, newName);
        genreService.updateGenre(updatedGenre);

        ConsoleUtils.printSuccess("Жанр успешно обновлен!");
        ConsoleUtils.pause();
    }

    private void deleteGenre() {
        ConsoleUtils.printSubHeader("Удаление жанра");

        int genreId = validator.readInt("Введите ID жанра для удаления: ");
        Genre genre = genreService.getGenreById(genreId);

        if (genre == null) {
            ConsoleUtils.printError("Жанр с ID " + genreId + " не найден.");
        } else {
            System.out.println("\nВы собираетесь удалить жанр: " + genre.name());
            if (validator.readYesNo("Подтвердите удаление")) {
                genreService.deleteGenre(genreId);
                ConsoleUtils.printSuccess("Жанр успешно удален!");
            } else {
                ConsoleUtils.printInfo("Удаление отменено.");
            }
        }

        ConsoleUtils.pause();
    }
}
