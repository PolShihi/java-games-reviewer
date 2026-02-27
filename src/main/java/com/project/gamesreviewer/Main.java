package com.project.gamesreviewer;

import com.project.gamesreviewer.config.AppConfig;
import com.project.gamesreviewer.ui.MainMenu;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        try {
            MainMenu mainMenu = context.getBean(MainMenu.class);
            mainMenu.show();
        } catch (Exception e) {
            System.err.println("\nОшибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (context instanceof AnnotationConfigApplicationContext) {
                ((AnnotationConfigApplicationContext) context).close();
            }
        }
    }
}
