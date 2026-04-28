package ru.hexaend.ui.command.impl;

import ru.hexaend.service.PhoneBookService;
import ru.hexaend.ui.command.Command;
import ru.hexaend.ui.command.ConsoleHelper;

/**
 * Команда отображения всех контактов справочника.
 *
 * @author Vasily Melnik
 */
public class ShowAllCommand implements Command {

  private final PhoneBookService service;
  private final ConsoleHelper helper;

  public ShowAllCommand(PhoneBookService service, ConsoleHelper helper) {
    this.service = service;
    this.helper = helper;
  }

  @Override
  public void execute() {
    helper.printContacts(service.getAllContacts(), "Справочник пуст.");
  }
}
