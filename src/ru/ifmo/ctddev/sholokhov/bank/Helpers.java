package ru.ifmo.ctddev.sholokhov.bank;

import java.rmi.RemoteException;
import java.util.function.Predicate;

/**
 * This class represents utility set for operating with lambdas that
 * throw {@link java.rmi.RemoteException}.
 */
public class Helpers {
    @FunctionalInterface
    public interface RemotePredicate<T> {
        Boolean apply(T in) throws RemoteException;
    }

    public static <T> Predicate<T> valOrDef(RemotePredicate<T> in, boolean defaultValue) {
        return p -> {
            try {
                return in.apply(p);
            } catch (RemoteException ignored) { return defaultValue;}
        };
    }

    public static <T> Predicate<T> valOrDef(RemotePredicate<T> in) {
        return valOrDef(in, false);
    }
}
