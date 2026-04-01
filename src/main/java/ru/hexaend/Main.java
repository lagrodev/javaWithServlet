package ru.hexaend;

import ru.hexaend.repository.ContactRepository;
import ru.hexaend.repository.impl.InMemoryContactRepository;
import ru.hexaend.service.PhoneBookService;
import ru.hexaend.util.PhoneValidator;
import ru.hexaend.service.impl.PhoneBookServiceImpl;
import ru.hexaend.util.impl.PhoneValidatorImpl;
import ru.hexaend.ui.ConsoleUi;

public class Main
{
    static void main()
    {
        PhoneValidator validator = new  PhoneValidatorImpl();
        ContactRepository repa = new InMemoryContactRepository();
        PhoneBookService phoneBookService = new PhoneBookServiceImpl(repa, validator);
        ConsoleUi consoleUi = new ConsoleUi(phoneBookService);
        consoleUi.start();

    }
}
