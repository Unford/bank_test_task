package by.clevertec.bank.model;

@FunctionalInterface
public interface ThrowingBiConsumer<T, V, E extends Exception> {
    void accept(T t, V v) throws E;
}
