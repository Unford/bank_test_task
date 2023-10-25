package by.clevertec.bank.model;

@FunctionalInterface
public interface BiThrowingFunction<T, R, E extends Exception, M extends Exception> {
    R apply(T t) throws E, M;
}
