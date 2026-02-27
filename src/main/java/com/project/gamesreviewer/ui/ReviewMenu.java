package com.project.gamesreviewer.ui;

import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.exception.ForeignKeyViolationException;
import com.project.gamesreviewer.model.Game;
import com.project.gamesreviewer.model.MediaOutlet;
import com.project.gamesreviewer.model.Review;
import com.project.gamesreviewer.service.GameService;
import com.project.gamesreviewer.service.MediaOutletService;
import com.project.gamesreviewer.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ReviewMenu {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private GameService gameService;

    @Autowired
    private MediaOutletService mediaOutletService;

    private final Scanner scanner;
    private final InputValidator validator;

    public ReviewMenu() {
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator(scanner);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("УПРАВЛЕНИЕ ОБЗОРАМИ");

            System.out.println("1. Показать все обзоры");
            System.out.println("2. Найти обзор по ID");
            System.out.println("3. Показать обзоры для игры");
            System.out.println("4. Добавить новый обзор");
            System.out.println("5. Редактировать обзор");
            System.out.println("6. Удалить обзор");
            System.out.println("0. Назад в главное меню");

            int choice = validator.readInt("\nВаш выбор: ", 0, 6);

            switch (choice) {
                case 1 -> showAllReviews();
                case 2 -> findReviewById();
                case 3 -> showReviewsForGame();
                case 4 -> createReview();
                case 5 -> updateReview();
                case 6 -> deleteReview();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void showAllReviews() {
        ConsoleUtils.printSubHeader("Список всех обзоров");

        List<Review> reviews = reviewService.getAllReviews();

        if (reviews.isEmpty()) {
            ConsoleUtils.printWarning("Обзоры не найдены.");
        } else {
            String[] headers = {"ID", "Игра", "Издание", "Рейтинг", "Резюме"};
            String[][] data = new String[reviews.size()][5];

            for (int i = 0; i < reviews.size(); i++) {
                Review review = reviews.get(i);
                data[i][0] = String.valueOf(review.id());
                data[i][1] = ConsoleUtils.truncate(review.gameTitle(), 25);
                data[i][2] = ConsoleUtils.truncate(review.mediaOutletName(), 20);
                data[i][3] = String.valueOf(review.score());
                data[i][4] = ConsoleUtils.truncate(review.summary(), 25);
            }

            ConsoleUtils.printTable(headers, data);
            ConsoleUtils.printInfo("Всего обзоров: " + reviews.size());
        }

        ConsoleUtils.pause();
    }

    private void findReviewById() {
        ConsoleUtils.printSubHeader("Поиск обзора по ID");

        int reviewId = validator.readInt("Введите ID обзора: ");
        Review review = reviewService.getReviewById(reviewId);

        if (review == null) {
            ConsoleUtils.printError("Обзор с ID " + reviewId + " не найден.");
        } else {
            printReviewDetails(review);
        }

        ConsoleUtils.pause();
    }

    private void showReviewsForGame() {
        ConsoleUtils.printSubHeader("Обзоры для игры");

        int gameId = validator.readInt("Введите ID игры: ");
        Game game = gameService.getGameById(gameId);
        
        if (game == null) {
            ConsoleUtils.printError("Игра с ID " + gameId + " не найдена.");
            ConsoleUtils.pause();
            return;
        }
        
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);

        System.out.println("\nИгра: " + game.title() + " (" + game.releaseYear() + ")");
        
        if (reviews.isEmpty()) {
            ConsoleUtils.printWarning("Обзоры для этой игры не найдены.");
        } else {
            Double avgScore = reviewService.getAverageScoreForGame(gameId);
            System.out.println("Средний рейтинг: " + (avgScore != null ? String.format("%.2f", avgScore) : "N/A"));
            System.out.println();

            for (Review review : reviews) {
                printReviewDetails(review);
                System.out.println();
            }
        }

        ConsoleUtils.pause();
    }

    private void printReviewDetails(Review review) {
        ConsoleUtils.printSeparator();
        System.out.println("ID: " + review.id());
        System.out.println("Игра: " + review.gameTitle());
        System.out.println("Издание: " + review.mediaOutletName());
        System.out.println("Рейтинг: " + review.score() + "/100");
        if (review.summary() != null && !review.summary().isEmpty()) {
            System.out.println("\nРезюме:");
            System.out.println(review.summary());
        }
        ConsoleUtils.printSeparator();
    }

    private void createReview() {
        ConsoleUtils.printSubHeader("Добавление нового обзора");

        List<Game> games = gameService.getAllGames();
        if (games.isEmpty()) {
            ConsoleUtils.printError("В базе нет игр. Сначала создайте игры.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nДоступные игры:");
        for (Game game : games) {
            System.out.printf("%d. %s (%d)%n", game.id(), game.title(), game.releaseYear());
        }

        int gameId = validator.readInt("\nВыберите игру (ID): ");

        List<MediaOutlet> outlets = mediaOutletService.getAllMediaOutlets();
        if (outlets.isEmpty()) {
            ConsoleUtils.printError("В базе нет медиа-изданий. Сначала создайте издания.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nДоступные издания:");
        for (MediaOutlet outlet : outlets) {
            System.out.printf("%d. %s%n", outlet.id(), outlet.name());
        }

        int outletId = validator.readInt("\nВыберите издание (ID): ");

        int score = validator.readInt("Рейтинг (0-100): ", 0, 100);
        String summary = validator.readStringOrNull("Резюме обзора (Enter для пропуска): ");

        Review newReview = new Review(0, gameId, outletId, score, summary);
        
        try {
            int reviewId = reviewService.createReview(newReview);
            ConsoleUtils.printSuccess("Обзор успешно создан с ID: " + reviewId);
        } catch (DuplicateEntryException | ForeignKeyViolationException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void updateReview() {
        ConsoleUtils.printSubHeader("Редактирование обзора");

        int reviewId = validator.readInt("Введите ID обзора: ");
        Review review = reviewService.getReviewById(reviewId);

        if (review == null) {
            ConsoleUtils.printError("Обзор с ID " + reviewId + " не найден.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nТекущие данные:");
        System.out.println("Рейтинг: " + review.score());
        System.out.println("Резюме: " + (review.summary() != null ? review.summary() : "Нет"));

        System.out.println("\nОставьте поле пустым, чтобы не изменять значение.");

        String scoreInput = validator.readString("Новый рейтинг (0-100): ");
        int newScore = scoreInput.isEmpty() ? review.score() : Integer.parseInt(scoreInput);

        String newSummary = validator.readString("Новое резюме: ");
        if (newSummary.isEmpty()) newSummary = review.summary();

        Review updatedReview = new Review(
                reviewId, review.gameId(), review.mediaOutletId(),
                newScore, newSummary
        );

        reviewService.updateReview(updatedReview);
        ConsoleUtils.printSuccess("Обзор успешно обновлен!");
        ConsoleUtils.pause();
    }

    private void deleteReview() {
        ConsoleUtils.printSubHeader("Удаление обзора");

        int reviewId = validator.readInt("Введите ID обзора для удаления: ");
        Review review = reviewService.getReviewById(reviewId);

        if (review == null) {
            ConsoleUtils.printError("Обзор с ID " + reviewId + " не найден.");
        } else {
            System.out.println("\nВы собираетесь удалить обзор:");
            System.out.println("Игра: " + review.gameTitle());
            System.out.println("Издание: " + review.mediaOutletName());
            System.out.println("Рейтинг: " + review.score());

            if (validator.readYesNo("Подтвердите удаление")) {
                reviewService.deleteReview(reviewId);
                ConsoleUtils.printSuccess("Обзор успешно удален!");
            } else {
                ConsoleUtils.printInfo("Удаление отменено.");
            }
        }

        ConsoleUtils.pause();
    }
}
