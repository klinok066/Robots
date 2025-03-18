package serialization;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Класс, представляющий состояние объекта.
 * Объект состоит из записей, которые хранятся в map.
 */
public class State {
    /**
     * Map, хранящая записи объекта.
     * Ключом является имя свойства, значением - значение свойства.
     */
    private final Map<String, Object> map;

    /**
     * Конструктор класса State.
     * Создает пустую map для хранения записей объекта.
     */
    public State() {
        this.map = new HashMap<>();
    }

    /**
     * Возвращает значение свойства объекта по его имени.
     *
     * @param propertyName имя свойства, значение которого необходимо получить.
     * @return значение свойства объекта.
     */
    public Object getProperty(String propertyName) {
        return map.get(propertyName);
    }

    /**
     * Устанавливает значение свойства объекта по его имени.
     *
     * @param propertyName имя свойства, значение которого необходимо установить.
     * @param property     значение свойства, которое необходимо установить.
     */
    public void setProperty(String propertyName, Object property) {
        map.put(propertyName, property);
    }

    /**
     * Возвращает множество имен свойств объекта.
     *
     * @return множество имен свойств объекта.
     */

    public Set<String> getKeys() {
        return map.keySet();
    }
}