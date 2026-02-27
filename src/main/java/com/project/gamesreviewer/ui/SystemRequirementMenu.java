package com.project.gamesreviewer.ui;

import com.project.gamesreviewer.exception.DuplicateEntryException;
import com.project.gamesreviewer.exception.ForeignKeyViolationException;
import com.project.gamesreviewer.model.Game;
import com.project.gamesreviewer.model.SystemRequirement;
import com.project.gamesreviewer.model.SystemRequirementType;
import com.project.gamesreviewer.service.GameService;
import com.project.gamesreviewer.service.SystemRequirementService;
import com.project.gamesreviewer.service.SystemRequirementTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class SystemRequirementMenu {

    @Autowired
    private SystemRequirementService requirementService;

    @Autowired
    private GameService gameService;

    @Autowired
    private SystemRequirementTypeService requirementTypeService;

    private final Scanner scanner;
    private final InputValidator validator;

    public SystemRequirementMenu() {
        this.scanner = new Scanner(System.in);
        this.validator = new InputValidator(scanner);
    }

    public void show() {
        while (true) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("УПРАВЛЕНИЕ СИСТЕМНЫМИ ТРЕБОВАНИЯМИ");

            System.out.println("1. Показать все требования");
            System.out.println("2. Найти требование по ID");
            System.out.println("3. Показать требования для игры");
            System.out.println("4. Добавить новое требование");
            System.out.println("5. Редактировать требование");
            System.out.println("6. Удалить требование");
            System.out.println("0. Назад в главное меню");

            int choice = validator.readInt("\nВаш выбор: ", 0, 6);

            switch (choice) {
                case 1 -> showAllRequirements();
                case 2 -> findRequirementById();
                case 3 -> showRequirementsForGame();
                case 4 -> createRequirement();
                case 5 -> updateRequirement();
                case 6 -> deleteRequirement();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void showAllRequirements() {
        ConsoleUtils.printSubHeader("Список всех системных требований");

        List<SystemRequirement> requirements = requirementService.getAllRequirements();

        if (requirements.isEmpty()) {
            ConsoleUtils.printWarning("Системные требования не найдены.");
        } else {
            String[] headers = {"ID", "Игра", "Профиль", "HDD (GB)", "RAM (GB)", "CPU (GHz)", "GPU (TF)"};
            String[][] data = new String[requirements.size()][7];

            for (int i = 0; i < requirements.size(); i++) {
                SystemRequirement req = requirements.get(i);
                data[i][0] = String.valueOf(req.id());
                data[i][1] = ConsoleUtils.truncate(req.gameTitle(), 25);
                data[i][2] = req.requirementType();
                data[i][3] = String.valueOf(req.storageGb());
                data[i][4] = String.valueOf(req.ramGb());
                data[i][5] = req.cpuGhz() != null ? String.valueOf(req.cpuGhz()) : "N/A";
                data[i][6] = req.gpuTflops() != null ? String.valueOf(req.gpuTflops()) : "N/A";
            }

            ConsoleUtils.printTable(headers, data);
            ConsoleUtils.printInfo("Всего требований: " + requirements.size());
        }

        ConsoleUtils.pause();
    }

    private void findRequirementById() {
        ConsoleUtils.printSubHeader("Поиск требования по ID");

        int reqId = validator.readInt("Введите ID требования: ");
        SystemRequirement req = requirementService.getRequirementById(reqId);

        if (req == null) {
            ConsoleUtils.printError("Требование с ID " + reqId + " не найдено.");
        } else {
            printRequirementDetails(req);
        }

        ConsoleUtils.pause();
    }

    private void showRequirementsForGame() {
        ConsoleUtils.printSubHeader("Системные требования для игры");

        int gameId = validator.readInt("Введите ID игры: ");
        List<SystemRequirement> requirements = requirementService.getRequirementsByGameId(gameId);

        if (requirements.isEmpty()) {
            ConsoleUtils.printWarning("Системные требования для этой игры не найдены.");
        } else {
            Game game = gameService.getGameById(gameId);
            if (game != null) {
                System.out.println("\nИгра: " + game.title());
            }
            System.out.println();

            for (SystemRequirement req : requirements) {
                printRequirementDetails(req);
                System.out.println();
            }
        }

        ConsoleUtils.pause();
    }

    private void printRequirementDetails(SystemRequirement req) {
        ConsoleUtils.printSeparator();
        System.out.println("ID: " + req.id());
        if (req.gameTitle() != null) {
            System.out.println("Игра: " + req.gameTitle());
        }
        System.out.println("Профиль: " + req.requirementType());
        System.out.println("Хранилище: " + req.storageGb() + " GB");
        System.out.println("RAM: " + req.ramGb() + " GB");
        if (req.cpuGhz() != null) {
            System.out.println("CPU: " + req.cpuGhz() + " GHz");
        }
        if (req.gpuTflops() != null) {
            System.out.println("GPU: " + req.gpuTflops() + " TFlops");
        }
        if (req.vramGb() != null) {
            System.out.println("VRAM: " + req.vramGb() + " GB");
        }
        ConsoleUtils.printSeparator();
    }

    private void createRequirement() {
        ConsoleUtils.printSubHeader("Добавление нового требования");

        List<Game> games = gameService.getAllGames();
        if (games.isEmpty()) {
            ConsoleUtils.printError("В базе нет игр. Сначала создайте игры.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nДоступные игры:");
        for (Game game : games) {
            System.out.printf("%d. %s%n", game.id(), game.title());
        }

        int gameId = validator.readInt("\nВыберите игру (ID): ");

        List<SystemRequirementType> types = requirementTypeService.getAllSystemRequirementTypes();
        System.out.println("\nТипы требований:");
        for (SystemRequirementType type : types) {
            System.out.printf("%d. %s%n", type.id(), type.name());
        }

        int typeId = validator.readInt("\nВыберите тип (ID): ");

        int storageGb = validator.readInt("Хранилище (GB): ", 1, 1000);
        int ramGb = validator.readInt("RAM (GB): ", 1, 256);
        Double cpuGhz = validator.readDoubleOrNull("CPU (GHz, Enter для пропуска): ");
        Double gpuTflops = validator.readDoubleOrNull("GPU (TFlops, Enter для пропуска): ");
        Integer vramGb = validator.readIntOrNull("VRAM (GB, Enter для пропуска): ");

        SystemRequirement newReq = new SystemRequirement(
                0, gameId, typeId, storageGb, ramGb, cpuGhz, gpuTflops, vramGb, null
        );

        try {
            int reqId = requirementService.createRequirement(newReq);
            ConsoleUtils.printSuccess("Системное требование успешно создано с ID: " + reqId);
        } catch (DuplicateEntryException | ForeignKeyViolationException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void updateRequirement() {
        ConsoleUtils.printSubHeader("Редактирование требования");

        int reqId = validator.readInt("Введите ID требования: ");
        SystemRequirement req = requirementService.getRequirementById(reqId);

        if (req == null) {
            ConsoleUtils.printError("Требование с ID " + reqId + " не найдено.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println("\nТекущие данные:");
        System.out.println("Хранилище: " + req.storageGb() + " GB");
        System.out.println("RAM: " + req.ramGb() + " GB");
        System.out.println("CPU: " + (req.cpuGhz() != null ? req.cpuGhz() + " GHz" : "Нет"));
        System.out.println("GPU: " + (req.gpuTflops() != null ? req.gpuTflops() + " TFlops" : "Нет"));
        System.out.println("VRAM: " + (req.vramGb() != null ? req.vramGb() + " GB" : "Нет"));

        System.out.println("\nОставьте поле пустым, чтобы не изменять значение.");

        String storageInput = validator.readString("Новое хранилище (GB): ");
        int newStorage = storageInput.isEmpty() ? req.storageGb() : Integer.parseInt(storageInput);

        String ramInput = validator.readString("Новый RAM (GB): ");
        int newRam = ramInput.isEmpty() ? req.ramGb() : Integer.parseInt(ramInput);

        String cpuInput = validator.readString("Новый CPU (GHz): ");
        Double newCpu = cpuInput.isEmpty() ? req.cpuGhz() : Double.parseDouble(cpuInput);

        String gpuInput = validator.readString("Новый GPU (TFlops): ");
        Double newGpu = gpuInput.isEmpty() ? req.gpuTflops() : Double.parseDouble(gpuInput);

        String vramInput = validator.readString("Новый VRAM (GB): ");
        Integer newVram = vramInput.isEmpty() ? req.vramGb() : Integer.parseInt(vramInput);

        SystemRequirement updatedReq = new SystemRequirement(
                reqId, req.gameId(), req.systemRequirementTypeId(),
                newStorage, newRam, newCpu, newGpu, newVram, null
        );

        requirementService.updateRequirement(updatedReq);
        ConsoleUtils.printSuccess("Требование успешно обновлено!");
        ConsoleUtils.pause();
    }

    private void deleteRequirement() {
        ConsoleUtils.printSubHeader("Удаление требования");

        int reqId = validator.readInt("Введите ID требования для удаления: ");
        SystemRequirement req = requirementService.getRequirementById(reqId);

        if (req == null) {
            ConsoleUtils.printError("Требование с ID " + reqId + " не найдено.");
        } else {
            System.out.println("\nВы собираетесь удалить требование:");
            if (req.gameTitle() != null) {
                System.out.println("Игра: " + req.gameTitle());
            }
            System.out.println("Профиль: " + req.requirementType());

            if (validator.readYesNo("Подтвердите удаление")) {
                requirementService.deleteRequirement(reqId);
                ConsoleUtils.printSuccess("Требование успешно удалено!");
            } else {
                ConsoleUtils.printInfo("Удаление отменено.");
            }
        }

        ConsoleUtils.pause();
    }
}
