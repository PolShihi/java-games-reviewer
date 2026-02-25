package com.project.gamesreviewer;

import com.project.gamesreviewer.config.AppConfig;
import com.project.gamesreviewer.model.Game;
import com.project.gamesreviewer.service.GameService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        try {
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            System.out.println("Spring context initialized successfully!\n");
            
            GameService gameService = context.getBean(GameService.class);
            List<Game> games = gameService.getAllGames();
            
            if (games.isEmpty()) {
                System.out.println("No games found in database.");
            } else {
                System.out.println("Successfully fetched " + games.size());
                printGamesTable(games);
            }
            
        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            System.err.println("\nStack trace:");
            e.printStackTrace();
        }
    }
    
    private static void printGamesTable(List<Game> games) {
        System.out.printf("%-4s | %-30s | %-6s | %-20s | %-20s%n", 
                "ID", "Title", "Year", "Developer", "Publisher");
        System.out.println("-----|--------------------------------|--------|----------------------|----------------------");
        
        for (Game game : games) {
            System.out.printf("%-4d | %-30s | %-6d | %-20s | %-20s%n",
                    game.id(),
                    truncate(game.title(), 30),
                    game.releaseYear(),
                    truncate(game.developerName(), 20),
                    truncate(game.publisherName(), 20)
            );
        }
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "N/A";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}
