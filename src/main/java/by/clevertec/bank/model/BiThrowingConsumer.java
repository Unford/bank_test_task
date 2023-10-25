package by.clevertec.bank.model;

@FunctionalInterface
public interface BiThrowingConsumer<T, E extends Exception, M extends Exception> {
    void accept(T t) throws E, M;
}
