package ru.ifmo.ctddev.sholokhov.bank;

import static ru.ifmo.ctddev.sholokhov.bank.Helpers.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a client able to connect to the bank server, check user's ID,
 * retrieve user's balance, change it and more.
 */
public class RMIClient {
    private static final String howToUse = "Wrong howToUse: name surname id accountId [+|-]num";

    private static void dumpState(Bank bank, Person person) throws RemoteException {
        System.out.println();
        System.out.println(person.getName() + " " + person.getSurname());
        System.out.println("Your current account list: \n ID, Balance");
        bank.getAccounts(person).forEach(p -> {
            String balance = "Cannot retrieve balance";
            String id = "NO ID";
            try {
                balance = String.valueOf(p.getBalance());
                id = p.getId();
            } catch (RemoteException ignored) {}
            System.out.println(id + "   " + balance);
        });
        System.out.println();
    }

    /**
     * This method accepts params in the form: name surname id accountId [+|-]num
     * If user is not registered in bank, he's registered after invoking this method.
     * If ID doesn't match, the error is shown.
     * If accountID stands for nonexistent account, new account with 0 balance is created.
     * If account was present, the balance is changed due to the sum specified in the last argument.
     *
     * @param args  arguments that satisfy howToUse form
     */
    public static void main(String[] args) {
        if (args == null || args.length != 5 || Arrays.stream(args).anyMatch(a -> a == null)
                || Arrays.stream(args).anyMatch(a -> a.length() == 0) || args[4].length() < 2) {
            System.err.println(howToUse);
            return;
        }
        String name = args[0];
        String surname = args[1];
        String id = args[2];
        String accountId = args[3];

        char deltaSign;
        long delta;
        try {
            deltaSign = args[4].charAt(0);
            if (deltaSign != '+' && deltaSign != '-') {
                throw new NumberFormatException();
            }
            delta = Long.parseLong(args[4].substring(1, args[4].length()));
        } catch (NumberFormatException e) {
            System.out.println("Wrong number format: \n" + howToUse);
            return;
        }
        try {
            Bank bank = (Bank) Naming.lookup("rmi://localhost/bank");
            Person person;
            PersonType currentType = PersonType.Local;

            if (currentType == PersonType.Remote) {
                person = new RemotePerson(name, surname, id);
            } else {
                person = new LocalPerson(name, surname, id);
            }



            List<Person> personList = bank.searchPersonByName(person.getName(), person.getSurname(), person.getType());


            boolean newPerson = false;
            if (personList.isEmpty()) {
                bank.addAccount(person, accountId);
                System.out.println("Created new account with id = " + accountId + " with balance = 0");
                newPerson = true;
                if (currentType == PersonType.Remote) unexportPerson(person);
            }
            //System.out.println("Aaaaa");
            if (personList.stream().noneMatch(valOrDef(p -> p.getId().equals(person.getId())))) {
                if (!newPerson) System.out.println("Your id doesn't match");
                if (currentType == PersonType.Remote) unexportPerson(person);
            }

            List<Account> accounts = bank.getAccounts(person);
            Optional<Account> current = accounts.stream().filter(valOrDef(p -> p.getId().equals(accountId))).findFirst();
            if (!current.isPresent()) {
                bank.addAccount(person, accountId);
                System.out.println("Created new account with id " + accountId + " with balance 0");
                if (currentType == PersonType.Remote) unexportPerson(person);
            }

            Account account = current.get();
            Long balance = account.getBalance();

            System.out.println("Balance on account " + accountId + " before update: " + balance);
            if (deltaSign == '+') {
                account.increaseBalance(delta);
            } else {
                account.decreaseBalance(delta);
            }
            System.out.println("Current balance: " + bank.getBalance(person, accountId));

            dumpState(bank, person);

            if (currentType == PersonType.Remote) unexportPerson(person);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    static void unexportPerson(Person person) {
        try {
            if (!UnicastRemoteObject.unexportObject(person, false)) {
                System.err.println("Failed to unexport person");
            }
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
