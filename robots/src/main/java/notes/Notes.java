package notes;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * Класс Notes представляет собой коллекцию заметок.
 * Коллекция ограничена заданным размером, при превышении которого
 * самая старая заметка удаляется для освобождения места.
 * Заметки хранятся в порядке добавления и доступны для итерации.
 * Поддерживает операции добавления, получения, удаления и проверки наличия элементов.
 * Реализует интерфейс Iterable для поддержки циклов for-each и метода forEach.
 *
 * @param <Type> тип элементов в коллекции Notes
 */
public class Notes<Type> implements Iterable<Type> {
    /**
     * Внутренний класс LinkedElement представляет элемент связного списка.
     *
     * @param <Type> тип значения, хранящегося в элементе
     */
    private static class LinkedElement<Type> {
        volatile Type value;
        volatile boolean exist;

        volatile int number;

        volatile LinkedElement<Type> prev;
        volatile LinkedElement<Type> next;
    }

    /**
     * Класс NotesIterator представляет собой итератор для коллекции Notes.
     * Позволяет последовательно перебирать элементы коллекции.
     *
     * @param <Type> тип элементов в коллекции Notes
     */
    private static class NotesIterator<Type> implements Iterator<Type> {
        private LinkedElement<Type> next;
        private LinkedElement<Type> curr;

        private final Semaphore SEMAPHORE = new Semaphore(1, true);
        /**
         * Создает новый объект класса NotesIterator с указанным элементом.
         *
         * @param element элемент, с которого начинается итерация
         */
        public NotesIterator(LinkedElement<Type> element) {
            this.next = element;
            this.curr = null;
        }

        /**
         * Поиск следующего существующего элемента в итерации.
         *
         * @return следующий существующий элемент в итерации
         */
        private LinkedElement<Type> findNext() {
            try {
                SEMAPHORE.acquire();
            }   catch (InterruptedException e) {
                e.printStackTrace();
            }

            LinkedElement<Type> item = this.next;

            while (item.exist && item.next != null) {
                item = item.next;
            }

            SEMAPHORE.release();
            return item;
        }

        /**
         * Возвращает следующий элемент в итерации.
         *
         * @return следующий элемент в итерации
         */
        @Override
        public boolean hasNext() {
            return findNext().exist;
        }

        /**
         * Возвращает следующий элемент в итерации и перемещает указатель на следующий элемент.
         *
         * @return следующий элемент в итерации
         * @throws NoSuchElementException если достигнут конец коллекции и нет следующего элемента
         */
        @Override
        public Type next() {
            LinkedElement<Type> next = findNext();

            if (!findNext().exist) {
                throw new NoSuchElementException();
            }

            this.curr = next;
            this.next = next.next;

            return this.curr.value;
        }

        /**
         * Удаляет текущий элемент из коллекции.
         *
         * @throws UnsupportedOperationException всегда выбрасывает исключение, так как операция не поддерживается
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final Map<Type, LinkedElement<Type>> items;
    private final Map<Integer, Type> byIndex;
    private LinkedElement<Type> head;
    private LinkedElement<Type> placeholder;
    private int size = 10;
    private int count;
    private int start;
    private final Semaphore SEMAPHORE = new Semaphore(1, true);
    /**
     * Создает новый объект класса Notes с заданным размером.
     * Если указанный размер превышает 10, устанавливается указанный размер,
     * в противном случае используется размер по умолчанию (10).
     *
     * @param size размер коллекции Notes
     */
    public Notes(int size) {
        if (size > 10)
            this.size = size;

        items = new HashMap<>();
        byIndex = new HashMap<>();
        placeholder = new LinkedElement<>();
        placeholder.number = 0;
        placeholder.exist = false;
        head = placeholder;
        start = 0;
    }

    /**
     * Проверяет, является ли коллекция пустой.
     *
     * @return true, если коллекция пуста, в противном случае - false
     */
    public boolean isEmpty() {
        return head == placeholder;
    }

    /**
     * Возвращает количество элементов в коллекции.
     *
     * @return количество элементов в коллекции
     */
    public int size() {
        return count;
    }

    /**
     * Проверяет, содержит ли коллекция указанный элемент.
     *
     * @param o элемент, наличие которого нужно проверить
     * @return true, если элемент содержится в коллекции, в противном случае - false
     */
    public boolean containsValue(Object o) {
        if (o == null) {
            return false;
        }

        try {
            SEMAPHORE.acquire();
            Type other = (Type) o;
            boolean isContains = items.containsKey(other);
            SEMAPHORE.release();

            return isContains;
        } catch (ClassCastException e) {
            // just ignore
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Добавляет элемент в коллекцию.
     * Если размер коллекции превышает заданный размер,
     * самая старая заметка удаляется для освобождения места.
     *
     * @param element элемент, который нужно добавить
     * @return true, если элемент успешно добавлен, в противном случае - false
     */
    public boolean add(Type element) {
        try {
            SEMAPHORE.acquire();
            if (size == count) {
                items.remove(head.value);
                byIndex.remove(head.number);
                head.exist = false;
                head.next.prev = null;
                head = head.next;
                start = head.number;
                count--;
            }

            LinkedElement<Type> current = placeholder;
            current.value = element;
            current.exist = true;

            byIndex.put(current.number, element);
            items.put(element, current);

            current.next = new LinkedElement<>();

            placeholder = current.next;
            placeholder.prev = current;
            placeholder.exist = false;
            placeholder.number = current.number + 1;

            count++;

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Возвращает сегмент коллекции, начиная с указанного начального индекса
     * и заканчивая указанным конечным индексом.
     *
     * @param beginIndex начальный индекс сегмента (включительно)
     * @param endIndex   конечный индекс сегмента (включительно)
     * @return список элементов сегмента коллекции
     * @throws IndexOutOfBoundsException если начальный или конечный индекс выходит за границы коллекции
     */
    public List<Type> getSegment(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > (size() - 1)) {
            throw new IndexOutOfBoundsException();
        }

        List<Type> segment = new ArrayList<>();

        try {
            SEMAPHORE.acquire();

            for (int i = start + beginIndex; i <= start + endIndex; i++) {
                segment.add(get(i));
            }
            SEMAPHORE.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return segment;
    }

    /**
     * Возвращает последний элемент коллекции без его удаления.
     *
     * @return последний элемент коллекции или null, если коллекция пуста
     */
    public Type peek() {
        try {
            SEMAPHORE.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (head == placeholder) {
            return null;
        }

        Type value = placeholder.prev.value;
        SEMAPHORE.release();
        return value;
    }

    /**
     * Удаляет и возвращает последний элемент коллекции.
     *
     * @return удаленный элемент или null, если коллекция пуста
     */
    public Type pop() {
        try {
            SEMAPHORE.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (head == placeholder) {
            return null;
        }

        LinkedElement<Type> lastElement = placeholder.prev;

        Type returnValue = lastElement.value;

        items.remove(lastElement.value);
        byIndex.remove(lastElement.number);
        lastElement.exist = false;

        if (count > 1) {
            lastElement.prev.next = placeholder;
            placeholder.prev = lastElement.prev;
            lastElement.prev = null;

            count--;

            return returnValue;
        }

        count--;

        head = placeholder;
        head.prev = null;

        return returnValue;
    }

    /**
     * Возвращает элемент коллекции по указанному индексу.
     *
     * @param index индекс элемента
     * @return элемент коллекции
     * @throws IndexOutOfBoundsException если индекс выходит за границы коллекции
     */
    public Type get(int index) {
        try {
            SEMAPHORE.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Type value = byIndex.get(start + index);

        SEMAPHORE.release();

        if (value == null) {
            throw new IndexOutOfBoundsException();
        }

        return value;
    }

    /**
     * Возвращает итератор для итерации по элементам коллекции.
     *
     * @return итератор для итерации по элементам коллекции
     */
    @Override
    public Iterator<Type> iterator() {
        return new NotesIterator<>(head);
    }


    /**
     * Выполняет заданное действие для каждого элемента коллекции.
     *
     * @param action действие, которое нужно выполнить для каждого элемента
     * @throws NullPointerException если переданное действие равно null
     */
    @Override
    public void forEach(Consumer<? super Type> action) {
        if (action == null) {
            throw new NullPointerException();
        }

        for (Type current : this) {
            action.accept(current);
        }
    }


    /**
     * Возвращает строковое представление коллекции.
     *
     * @return строковое представление коллекции
     */
    @Override
    public final String toString() {
        StringBuilder view = new StringBuilder();

        view.append("[");

        for (Type current : this) {
            view.append(current.toString());

            if (size() > 1 && (items.get(current).number < (start + size() - 1))) {
                view.append(", ");
            }
        }

        view.append("]");

        return view.toString();
    }
}