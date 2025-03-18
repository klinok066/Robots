package serialization;

/**
 * Интерфейс, реализующий сериализацию объекта
 */
public interface Saveable {

    /**
     * Получает текущее состояние объекта в виде экземпляра класса State.
     */
    State getState();

    /**
     * Получает имя объекта.
     */
    String getName();

    /**
     * Загружает состояние объекта из экземпляра класса State.
     *
     * @param state экземпляр класса State, содержащий состояние объекта.
     */
    void loadState(State state);
}