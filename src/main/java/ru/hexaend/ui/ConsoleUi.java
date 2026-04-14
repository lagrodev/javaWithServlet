package ru.hexaend.ui;

import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.*;
import ru.hexaend.ui.command.impl.*;

import java.util.Map;
import java.util.Scanner;

import static ru.hexaend.ui.command.ConsoleHelper.HEADER;
import static ru.hexaend.ui.command.ConsoleHelper.SEPARATOR;

public class ConsoleUi {

    private final ConsoleHelper helper;
    private final Map<String, Command> commands;

    public ConsoleUi(PhoneBookService service) {
        this.helper = new ConsoleHelper(new Scanner(System.in));
        this.commands = Map.of(
                "1", new ShowAllCommand(service, helper),
                "2", new AddContactCommand(service, helper),
                "3", new EditContactCommand(service, helper),
                "4", new DeleteContactCommand(service, helper),
                "5", new SearchByLastNameCommand(service, helper),
                "6", new SearchByPhoneCommand(service, helper),
                "7", new AddPhoneCommand(service, helper)
        );
    }

    public void start() {
        System.out.println(HEADER);
        System.out.println("           ТЕЛЕФОННЫЙ СПРАВОЧНИК");
        System.out.println(HEADER);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = helper.readLine("Выберите пункт меню: ");
            System.out.println();
            if ("0".equals(choice))
            {
                running = false;
            }
            else
            {
                Command cmd = commands.get(choice);
                if (cmd != null)
                {
                    cmd.execute();
                }
                else
                {
                    System.out.println("  ⚠  Неверный пункт меню. Попробуйте снова.\n");
                }
            }
        }

        System.out.println("До свидания!");
    }

    private void printMenu() {
        System.out.println(SEPARATOR);
        System.out.println("  1. Показать все контакты");
        System.out.println("  2. Добавить контакт");
        System.out.println("  3. Редактировать контакт");
        System.out.println("  4. Удалить контакт");
        System.out.println("  5. Поиск по фамилии");
        System.out.println("  6. Поиск по номеру телефона");
        System.out.println("  7. Добавить номер к существующему контакту");
        System.out.println("  0. Выход");
        System.out.println(SEPARATOR);
    }
}