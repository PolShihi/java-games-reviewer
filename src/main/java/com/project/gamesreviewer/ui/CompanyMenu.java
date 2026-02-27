package com.project.gamesreviewer.ui;

import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.exception.ForeignKeyViolationException;
import com.project.gamesreviewer.model.CompanyType;
import com.project.gamesreviewer.model.ProductionCompany;
import com.project.gamesreviewer.service.CompanyTypeService;
import com.project.gamesreviewer.service.ProductionCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class CompanyMenu {

    @Autowired
    private ProductionCompanyService companyService;

    @Autowired
    private CompanyTypeService companyTypeService;

    private final Scanner scanner;
    private final InputValidator validator;

    public CompanyMenu() {
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator(scanner);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("УПРАВЛЕНИЕ КОМПАНИЯМИ");

            System.out.println("1. Показать все компании");
            System.out.println("2. Найти компанию по ID");
            System.out.println("3. Добавить новую компанию");
            System.out.println("4. Редактировать компанию");
            System.out.println("5. Удалить компанию");
            System.out.println("0. Назад в главное меню");

            int choice = validator.readInt("\nВаш выбор: ", 0, 5);

            switch (choice) {
                case 1 -> showAllCompanies();
                case 2 -> findCompanyById();
                case 3 -> createCompany();
                case 4 -> updateCompany();
                case 5 -> deleteCompany();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void showAllCompanies() {
        ConsoleUtils.printSubHeader("Список всех компаний");

        List<ProductionCompany> companies = companyService.getAllCompanies();

        if (companies.isEmpty()) {
            ConsoleUtils.printWarning("Компании не найдены.");
        } else {
            String[] headers = {"ID", "Название", "Год основания", "CEO", "Тип"};
            String[][] data = new String[companies.size()][5];

            for (int i = 0; i < companies.size(); i++) {
                ProductionCompany company = companies.get(i);
                data[i][0] = String.valueOf(company.id());
                data[i][1] = ConsoleUtils.truncate(company.name(), 25);
                data[i][2] = company.foundedYear() != null ? String.valueOf(company.foundedYear()) : "N/A";
                data[i][3] = ConsoleUtils.truncate(company.ceo(), 20);
                data[i][4] = company.companyTypeName() != null ? company.companyTypeName() : "N/A";
            }

            ConsoleUtils.printTable(headers, data);
            ConsoleUtils.printInfo("Всего компаний: " + companies.size());
        }

        ConsoleUtils.pause();
    }

    private void findCompanyById() {
        ConsoleUtils.printSubHeader("Поиск компании по ID");

        int companyId = validator.readInt("Введите ID компании: ");
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

    private void createCompany() {
        ConsoleUtils.printSubHeader("Добавление новой компании");

        String name = validator.readNonEmptyString("Название компании: ");
        Integer foundedYear = validator.readYearFoundedOrNull("Год основания (Enter для пропуска): ");
        String websiteUrl = validator.readStringOrNull("Веб-сайт (Enter для пропуска): ");
        String ceo = validator.readStringOrNull("CEO (Enter для пропуска): ");

        List<CompanyType> types = companyTypeService.getAllCompanyTypes();
        System.out.println("\nТипы компаний:");
        for (CompanyType type : types) {
            System.out.printf("%d. %s%n", type.id(), type.name());
        }

        int companyTypeId = validator.readInt("\nВыберите тип компании (ID): ");

        ProductionCompany newCompany = new ProductionCompany(0, name, foundedYear, websiteUrl, ceo, companyTypeId);
        
        try {
            int companyId = companyService.createCompany(newCompany);
            ConsoleUtils.printSuccess("Компания успешно создана с ID: " + companyId);
        } catch (DuplicateEntryException | ForeignKeyViolationException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void updateCompany() {
        ConsoleUtils.printSubHeader("Редактирование компании");

        int companyId = validator.readInt("Введите ID компании: ");
        ProductionCompany company = companyService.getCompanyById(companyId);

        if (company == null) {
            ConsoleUtils.printError("Компания с ID " + companyId + " не найдена.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nТекущие данные:");
        System.out.println("Название: " + company.name());
        System.out.println("Год основания: " + (company.foundedYear() != null ? company.foundedYear() : "Нет"));
        System.out.println("Веб-сайт: " + (company.websiteUrl() != null ? company.websiteUrl() : "Нет"));
        System.out.println("CEO: " + (company.ceo() != null ? company.ceo() : "Нет"));

        System.out.println("\nОставьте поле пустым, чтобы не изменять значение.");

        String newName = validator.readString("Новое название: ");
        if (newName.isEmpty()) newName = company.name();

        Integer newYear = validator.readYearFoundedOrNull("Новый год основания (Enter для пропуска): ");
        if (newYear == null) newYear = company.foundedYear();

        String newWebsite = validator.readString("Новый веб-сайт: ");
        if (newWebsite.isEmpty()) newWebsite = company.websiteUrl();

        String newCeo = validator.readString("Новый CEO: ");
        if (newCeo.isEmpty()) newCeo = company.ceo();

        ProductionCompany updatedCompany = new ProductionCompany(
                companyId, newName, newYear, newWebsite, newCeo, company.companyTypeId()
        );

        companyService.updateCompany(updatedCompany);
        ConsoleUtils.printSuccess("Компания успешно обновлена!");
        ConsoleUtils.pause();
    }

    private void deleteCompany() {
        ConsoleUtils.printSubHeader("Удаление компании");

        int companyId = validator.readInt("Введите ID компании для удаления: ");
        ProductionCompany company = companyService.getCompanyById(companyId);

        if (company == null) {
            ConsoleUtils.printError("Компания с ID " + companyId + " не найдена.");
        } else {
            System.out.println("\nВы собираетесь удалить компанию: " + company.name());
            if (validator.readYesNo("Подтвердите удаление")) {
                companyService.deleteCompany(companyId);
                ConsoleUtils.printSuccess("Компания успешно удалена!");
            } else {
                ConsoleUtils.printInfo("Удаление отменено.");
            }
        }

        ConsoleUtils.pause();
    }
}
