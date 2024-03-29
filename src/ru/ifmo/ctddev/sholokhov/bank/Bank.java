package ru.ifmo.ctddev.sholokhov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface provides basic methods to implement bank system. The bank contains persons,
 * each person can have any number of accounts. It implements {@link java.rmi.Remote} interface,
 * so implementations can be accessed via rmi.
 */
public interface Bank extends Remote {
    /**
     * Add a person to bank. If the person is not registered in the bank, then
     * new empty account with zero balance. If person is registered and account
     * is not found, it's created with zero balance. If there's already an account
     * with specified id, nothing happens.
     *
     * @param person    person to add to bank system
     * @param accountId account id to add
     * @throws RemoteException
     */
    public void addAccount(Person person, String accountId) throws RemoteException;

    /**
     * This method search for the person with specified personal name, surname and type.
     * @param name          name of a person
     * @param surname       surname of a personz
     * @param type          type of a person
     * @return list of persons that have specified name, surname and type
     * @throws RemoteException
     */
    public List<Person> searchPersonByName(String name, String surname, PersonType type) throws RemoteException;

    /**
     * search person by id
      * @param id passport number
     * @return
     * @throws RemoteException
     */
    public List<Person> searchPersonByID(String id) throws RemoteException;

    /**
     * This method returns bunch of id related to accounts specified person has.
     * @param person person to retrieve accounts from
     * @return list of id of accounts that person has or null if person is not registered
     * @throws RemoteException
     */
    public List<Account> getAccounts(Person person) throws RemoteException;

    /**
     * This method returns current balance on account if found.
     * @param accountId id of account
     * @param person    person that owns the account
     * @return balance on account or null if account/person was not found
     * @throws RemoteException
     */
    public Long getBalance(Person person, String accountId) throws RemoteException;
}
