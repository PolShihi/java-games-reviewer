package com.project.gamesreviewer.ui;

import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.exception.ForeignKeyViolationException;
import com.project.gamesreviewer.model.*;
import com.project.gamesreviewer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class GameMenu {

    @Autowired
    private GameService gameService;

    @Autowired
    private ProductionCompanyService companyService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private SystemRequirementService requirementService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MediaOutletService mediaOutletService;

    private final Scanner scanner;
    private final InputValidator validator;

    public GameMenu() {
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator(scanner);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("УПРАВЛЕНИЕ ИГРАМИ");

            System.out.println("1. Показать все игры");
            System.out.println("2. Детальная информация об игре");
            System.out.println("3. Добавить новую игру");
            System.out.println("4. Редактировать игру");
            System.out.println("5. Удалить игру");
            System.out.println("0. Назад в главное меню");

            int choice = validator.readInt("\nВаш выбор: ", 0, 5);

            switch (choice) {
                case 1 -> showAllGames();
                case 2 -> showGameDetails();
                case 3 -> createGame();
                case 4 -> updateGame();
                case 5 -> deleteGame();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void showAllGames() {
        ConsoleUtils.printSubHeader("Список всех игр");

        List<Game> games = gameService.getAllGames();

        if (games.isEmpty()) {
            ConsoleUtils.printWarning("Игры не найдены.");
        } else {
            String[] headers = {"ID", "Название", "Год", "Разработчик", "Издатель"};
            String[][] data = new String[games.size()][5];

            for (int i = 0; i < games.size(); i++) {
                Game game = games.get(i);
                data[i][0] = String.valueOf(game.id());
                data[i][1] = ConsoleUtils.truncate(game.title(), 30);
                data[i][2] = String.valueOf(game.releaseYear());
                data[i][3] = ConsoleUtils.truncate(game.developerName() != null ? game.developerName() : "N/A", 20);
                data[i][4] = ConsoleUtils.truncate(game.publisherName() != null ? game.publisherName() : "N/A", 20);
            }

            ConsoleUtils.printTable(headers, data);
            ConsoleUtils.printInfo("Всего игр: " + games.size());
        }

        ConsoleUtils.pause();
    }

    private void showGameDetails() {
        ConsoleUtils.printSubHeader("Детальная информация об игре");

        int gameId = validator.readInt("Введите ID игры: ");
        showGameDetailsWithNavigation(gameId);
    }

    private void showGameDetailsWithNavigation(int gameId) {
        while (true) {
            Game game = gameService.getGameWithFullDetails(gameId);

            if (game == null) {
                ConsoleUtils.printError("Игра с ID " + gameId + " не найдена.");
                ConsoleUtils.pause();
                return;
            }

            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("Детальная информация об игре");

            System.out.println();
            ConsoleUtils.printSeparator();
            System.out.println("ID: " + game.id());
            System.out.println("Название: " + game.title());
            System.out.println("Год выпуска: " + game.releaseYear());
            
            if (game.developerId() != null && game.developerName() != null) {
                System.out.println("Разработчик: " + game.developerName() + " (ID: " + game.developerId() + ")");
            } else if (game.developerId() != null) {
                System.out.println("Разработчик: ID: " + game.developerId());
            } else {
                System.out.println("Разработчик: Не указан");
            }
            
            if (game.publisherId() != null && game.publisherName() != null) {
                System.out.println("Издатель: " + game.publisherName() + " (ID: " + game.publisherId() + ")");
            } else if (game.publisherId() != null) {
                System.out.println("Издатель: ID: " + game.publisherId());
            } else {
                System.out.println("Издатель: Не указан");
            }

            if (game.description() != null && !game.description().isEmpty()) {
                System.out.println("\nОписание:");
                System.out.println("  " + game.description());
            }

            if (game.genres() != null && !game.genres().isEmpty()) {
                System.out.println("\nЖанры: " + String.join(", ", game.genres()));
            } else {
                System.out.println("\nЖанры: Не указаны");
            }

            if (game.averageRating() != null) {
                System.out.println("Средний рейтинг: " + String.format("%.2f", game.averageRating()) + "/100");
            } else {
                System.out.println("Средний рейтинг: Нет обзоров");
            }

            List<SystemRequirement> requirements = gameService.getGameSystemRequirements(gameId);
            if (!requirements.isEmpty()) {
                System.out.println("\nСистемные требования:");
                for (SystemRequirement req : requirements) {
                    System.out.println("\n  Профиль: " + req.requirementType());
                    System.out.println("  Хранилище: " + req.storageGb() + " GB");
                    System.out.println("  RAM: " + req.ramGb() + " GB");
                    if (req.cpuGhz() != null) {
                        System.out.println("  CPU: " + req.cpuGhz() + " GHz");
                    }
                    if (req.gpuTflops() != null) {
                        System.out.println("  GPU: " + req.gpuTflops() + " TFlops");
                    }
                    if (req.vramGb() != null) {
                        System.out.println("  VRAM: " + req.vramGb() + " GB");
                    }
                }
            }
            ConsoleUtils.printSeparator();

            System.out.println("\nНавигация:");
            System.out.println("1. Просмотреть обзоры на эту игру");
            System.out.println("2. Просмотреть информацию о разработчике");
            System.out.println("3. Просмотреть информацию об издателе");
            System.out.println("0. Назад");

            int choice = validator.readInt("\nВаш выбор: ", 0, 3);

            switch (choice) {
                case 1 -> showGameReviews(gameId);
                case 2 -> {
                    if (game.developerId() != null) {
                        showCompanyDetails(game.developerId());
                    } else {
                        ConsoleUtils.printWarning("У игры не указан разработчик.");
                        ConsoleUtils.pause();
                    }
                }
                case 3 -> {
                    if (game.publisherId() != null) {
                        showCompanyDetails(game.publisherId());
                    } else {
                        ConsoleUtils.printWarning("У игры не указан издатель.");
                        ConsoleUtils.pause();
                    }
                }
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void showGameReviews(int gameId) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Обзоры на игру");

        List<Review> reviews = reviewService.getReviewsByGameId(gameId);

        if (reviews.isEmpty()) {
            ConsoleUtils.printWarning("Обзоры на эту игру не найдены.");
        } else {
            Double avgScore = reviewService.getAverageScoreForGame(gameId);
            System.out.println("\nСредний рейтинг: " + (avgScore != null ? String.format("%.2f", avgScore) : "N/A") + "/100");
            System.out.println("Всего обзоров: " + reviews.size());
            System.out.println();

            for (Review review : reviews) {
                ConsoleUtils.printSeparator();
                System.out.println("Издание: " + review.mediaOutletName() + " (ID: " + review.mediaOutletId() + ")");
                System.out.println("Рейтинг: " + review.score() + "/100");
                if (review.summary() != null && !review.summary().isEmpty()) {
                    System.out.println("Резюме: " + review.summary());
                }
            }
            ConsoleUtils.printSeparator();

            System.out.println("\nНавигация:");
            System.out.println("1. Просмотреть информацию об издании");
            System.out.println("0. Назад");

            int choice = validator.readInt("\nВаш выбор: ", 0, 1);

            if (choice == 1 && !reviews.isEmpty()) {
                int outletId = validator.readInt("Введите ID издания: ");
                showMediaOutletDetails(outletId);
            }
        }
    }

    private void showCompanyDetails(int companyId) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Информация о компании");

        ProductionCompany company = companyService.getCompanyById(companyId);

        if (company == null) {
            ConsoleUtils.printError("Компания с ID " + companyId + " не найдена.");
        } else {
            System.out.println();
            ConsoleUtils.printSeparator();
            System.out.println("ID: " + company.id());
            System.out.println("Название: " + company.name());
            if (company.foundedYear() != null) {
                System.out.println("Год основания: " + company.foundedYear());
            }
            if (company.websiteUrl() != null && !company.websiteUrl().isEmpty()) {
                System.out.println("Веб-сайт: " + company.websiteUrl());
            }
            if (company.ceo() != null && !company.ceo().isEmpty()) {
                System.out.println("CEO: " + company.ceo());
            }
            if (company.companyTypeName() != null) {
                System.out.println("Тип: " + company.companyTypeName());
            }
            ConsoleUtils.printSeparator();
        }

        ConsoleUtils.pause();
    }

    private void showMediaOutletDetails(int outletId) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Информация об издании");

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

    private void createGame() {
        ConsoleUtils.printSubHeader("Добавление новой игры");

        String title = validator.readNonEmptyString("Название игры: ");
        int releaseYear = validator.readYearRelease("Год выпуска: ");
        String description = validator.readStringOrNull("Описание (Enter для пропуска): ");

        Integer developerId = null;
        Integer publisherId = null;

        List<ProductionCompany> companies = companyService.getAllCompanies();
        if (companies.isEmpty()) {
            ConsoleUtils.printWarning("В базе нет компаний. Игра будет создана без разработчика и издателя.");
        } else {
            System.out.println("\nДоступные компании:");
            for (ProductionCompany company : companies) {
                String typeName = company.companyTypeName() != null ? company.companyTypeName() : "Unknown";
                System.out.printf("%d. %s (%s)%n", company.id(), company.name(), typeName);
            }
            System.out.println("0. Пропустить (NULL)");

            Integer devChoice = validator.readIntOrNull("\nВыберите разработчика (ID или 0 для пропуска): ");
            if (devChoice != null && devChoice != 0) {
                developerId = devChoice;
            }

            Integer pubChoice = validator.readIntOrNull("Выберите издателя (ID или 0 для пропуска): ");
            if (pubChoice != null && pubChoice != 0) {
                publisherId = pubChoice;
            }
        }

        Game newGame = new Game(0, title, releaseYear, description, developerId, publisherId, null, null, null, null);
        
        int gameId;
        try {
            gameId = gameService.createGame(newGame);
            ConsoleUtils.printSuccess("Игра успешно создана с ID: " + gameId);
        } catch (DuplicateEntryException | ForeignKeyViolationException e) {
            ConsoleUtils.printError(e.getMessage());
            ConsoleUtils.pause();
            return;
        }

        if (validator.readYesNo("\nДобавить жанры?")) {
            addGenresToGame(gameId);
        }

        if (validator.readYesNo("\nДобавить системные требования?")) {
            addSystemRequirements(gameId);
        }

        ConsoleUtils.pause();
    }

    private void addGenresToGame(int gameId) {
        List<Genre> allGenres = genreService.getAllGenres();
        if (allGenres.isEmpty()) {
            ConsoleUtils.printWarning("В базе нет жанров.");
            return;
        }

        System.out.println("\nДоступные жанры:");
        for (Genre genre : allGenres) {
            System.out.printf("%d. %s%n", genre.id(), genre.name());
        }

        System.out.println("\nВведите ID жанров через запятую (например: 1,3,5):");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return;
        }

        List<Integer> genreIds = new ArrayList<>();
        for (String id : input.split(",")) {
            try {
                genreIds.add(Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                ConsoleUtils.printWarning("Пропущен некорректный ID: " + id);
            }
        }

        if (!genreIds.isEmpty()) {
            gameService.updateGameGenres(gameId, genreIds);
            ConsoleUtils.printSuccess("Жанры добавлены!");
        }
    }

    private void addSystemRequirements(int gameId) {
        while (true) {
            System.out.println("\nТипы требований:");
            System.out.println("1. Low");
            System.out.println("2. Medium");
            System.out.println("3. High");
            System.out.println("0. Завершить добавление");

            int choice = validator.readInt("\nВыберите тип: ", 0, 3);
            if (choice == 0) break;

            int storageGb = validator.readInt("Хранилище (GB): ", 1, 500);
            int ramGb = validator.readInt("RAM (GB): ", 1, 128);
            Double cpuGhz = validator.readDoubleOrNull("CPU (GHz, Enter для пропуска): ");
            Double gpuTflops = validator.readDoubleOrNull("GPU (TFlops, Enter для пропуска): ");
            Integer vramGb = validator.readIntOrNull("VRAM (GB, Enter для пропуска): ");

            SystemRequirement req = new SystemRequirement(
                    0, gameId, choice, storageGb, ramGb, cpuGhz, gpuTflops, vramGb, null
            );

            requirementService.createRequirement(req);
            ConsoleUtils.printSuccess("Системные требования добавлены!");

            if (!validator.readYesNo("\nДобавить ещё профиль требований?")) {
                break;
            }
        }
    }

    private void updateGame() {
        ConsoleUtils.printSubHeader("Редактирование игры");

        int gameId = validator.readInt("Введите ID игры: ");
        Game game = gameService.getGameById(gameId);

        if (game == null) {
            ConsoleUtils.printError("Игра с ID " + gameId + " не найдена.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nТекущие данные:");
        System.out.println("Название: " + game.title());
        System.out.println("Год: " + game.releaseYear());
        System.out.println("Описание: " + (game.description() != null ? game.description() : "Нет"));
        System.out.println("Разработчик: " + (game.developerName() != null ? game.developerName() + " (ID: " + game.developerId() + ")" : "Не указан"));
        System.out.println("Издатель: " + (game.publisherName() != null ? game.publisherName() + " (ID: " + game.publisherId() + ")" : "Не указан"));

        System.out.println("\nОставьте поле пустым, чтобы не изменять значение.");

        String newTitle = validator.readString("Новое название: ");
        if (newTitle.isEmpty()) newTitle = game.title();

        Integer newYear = validator.readYearReleaseOrNull("Новый год (Enter для пропуска): ");
        if (newYear == null) newYear = game.releaseYear();

        String newDescription = validator.readString("Новое описание: ");
        if (newDescription.isEmpty()) newDescription = game.description();

        Integer newDeveloperId = game.developerId();
        Integer newPublisherId = game.publisherId();

        if (validator.readYesNo("\nИзменить разработчика?")) {
            List<ProductionCompany> companies = companyService.getAllCompanies();
            if (companies.isEmpty()) {
                ConsoleUtils.printWarning("В базе нет компаний.");
            } else {
                System.out.println("\nДоступные компании:");
                for (ProductionCompany company : companies) {
                    String typeName = company.companyTypeName() != null ? company.companyTypeName() : "Unknown";
                    System.out.printf("%d. %s (%s)%n", company.id(), company.name(), typeName);
                }
                System.out.println("0. Убрать разработчика (NULL)");
                
                Integer devChoice = validator.readIntOrNull("Выберите разработчика (ID или 0 для NULL): ");
                if (devChoice != null && devChoice == 0) {
                    newDeveloperId = null;
                } else if (devChoice != null) {
                    newDeveloperId = devChoice;
                }
            }
        }

        if (validator.readYesNo("\nИзменить издателя?")) {
            List<ProductionCompany> companies = companyService.getAllCompanies();
            if (companies.isEmpty()) {
                ConsoleUtils.printWarning("В базе нет компаний.");
            } else {
                System.out.println("\nДоступные компании:");
                for (ProductionCompany company : companies) {
                    String typeName = company.companyTypeName() != null ? company.companyTypeName() : "Unknown";
                    System.out.printf("%d. %s (%s)%n", company.id(), company.name(), typeName);
                }
                System.out.println("0. Убрать издателя (NULL)");
                
                Integer pubChoice = validator.readIntOrNull("Выберите издателя (ID или 0 для NULL): ");
                if (pubChoice != null && pubChoice == 0) {
                    newPublisherId = null;
                } else if (pubChoice != null) {
                    newPublisherId = pubChoice;
                }
            }
        }

        Game updatedGame = new Game(
                gameId, newTitle, newYear, newDescription,
                newDeveloperId, newPublisherId,
                null, null, null, null
        );

        try {
            gameService.updateGame(updatedGame);
            ConsoleUtils.printSuccess("Игра успешно обновлена!");
        } catch (DuplicateEntryException | ForeignKeyViolationException e) {
            ConsoleUtils.printError(e.getMessage());
            ConsoleUtils.pause();
            return;
        }

        if (validator.readYesNo("\nИзменить жанры?")) {
            addGenresToGame(gameId);
        }

        ConsoleUtils.pause();
    }

    private void deleteGame() {
        ConsoleUtils.printSubHeader("Удаление игры");

        int gameId = validator.readInt("Введите ID игры для удаления: ");
        Game game = gameService.getGameById(gameId);

        if (game == null) {
            ConsoleUtils.printError("Игра с ID " + gameId + " не найдена.");
        } else {
            System.out.println("\nВы собираетесь удалить игру: " + game.title());
            if (validator.readYesNo("Подтвердите удаление")) {
                gameService.deleteGame(gameId);
                ConsoleUtils.printSuccess("Игра успешно удалена!");
            } else {
                ConsoleUtils.printInfo("Удаление отменено.");
            }
        }

        ConsoleUtils.pause();
    }
}
