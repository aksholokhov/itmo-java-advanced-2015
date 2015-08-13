package ru.ifmo.ctddev.sholokhov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Basic implementation of {@link Person} interface.
 */
public class RemotePerson extends UnicastRemoteObject implements Person {
    private final LocalPerson person;

    public RemotePerson(String name, String surname, String id) throws RemoteException {
        super();
        this.person = new LocalPerson(name, surname, id);
    }

    @Override
    public String getName() throws RemoteException {
        return person.getName();
    }

    @Override
    public String getSurname() throws RemoteException {
        return person.getSurname();
    }

    @Override
    public String getId() throws RemoteException {
        return person.getId();
    }

    @Override
    public PersonType getType() throws RemoteException {
        return PersonType.Remote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemotePerson)) {
            return false;
        }

        RemotePerson that = (RemotePerson) o;

        if (person != null ? !person.equals(that.person) : that.person != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 123456789;
        result = 17 * result + (person != null ? person.hashCode() : 0);
        return result;
    }
}
