package serialization;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс StateHandler предоставляет методы для сохранения и загрузки состояния объектов Saveable.
 */
public class StateHandler {
    /**
     * Путь к файлу, где будет сохранено и откуда будет загружено состояние данных.
     */
    private final String path;

    /**
     * Создает новый объект StateHandler с указанным путем к файлу.
     *
     * @param path Путь к файлу, где будет сохранено и откуда будет загружено состояние данных.
     */
    public StateHandler(String path) {
        this.path = path;
    }

    /**
     * Создает объект JSONArray из списка объектов Saveable.
     *
     * @param saveableObjects Список объектов Saveable, которые будут преобразованы в JSONArray.
     * @return Объект JSONArray, содержащий данные состояния объектов Saveable.
     */
    private JSONArray constructJsonArray(List<Saveable> saveableObjects) {
        JSONArray jsonArray = new JSONArray();

        saveableObjects.forEach(object -> {
            State state = object.getState();
            JSONObject jsonUpperLevelMap = new JSONObject();
            JSONObject jsonLowerLevelMap = new JSONObject();
            state.getKeys().forEach(key -> {
                jsonLowerLevelMap.put(key, state.getProperty(key));
            });

            jsonUpperLevelMap.put(object.getName(), jsonLowerLevelMap);
            jsonArray.add(jsonUpperLevelMap);
        });
        return jsonArray;
    }

    /**
     * Создает объект State из объекта Map, содержащего ключи и значения свойств.
     *
     * @param map Объект Map, содержащий ключи и значения свойств.
     * @return Объект State, содержащий свойства, указанные в объекте map.
     */
    private State constructState(Map<String, Object> map) {
        State state = new State();
        map.keySet().forEach(key -> {
            state.setProperty(key, map.get(key));
        });
        return state;
    }


    /**
     * Сохраняет данные состояния списка объектов Saveable в файл.
     *
     * @param saveableObjects Список объектов Saveable, которые будут сохранены.
     */
    public void save(List<Saveable> saveableObjects) {
        JSONArray jsonArray = constructJsonArray(saveableObjects);
        try {
            File file = new File(path);
            file.createNewFile();
            file.setWritable(true);

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружает данные состояния всех объектов Saveable, сохраненных в файле.
     *
     * @return Объект Map, содержащий имена и объекты State всех объектов Saveable, сохраненных в файле.
     */
    public Map<String, State> loadAllData() {
        JSONParser jsonParser = new JSONParser();
        File file = new File(path);
        if (!file.exists())
            return null;

        try (FileReader fileReader = new FileReader(file)) {
            var rawData = jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) rawData;
            Map<String, State> stateMap = new HashMap<>();
            jsonArray.forEach(jsonObject -> {
                Map<String, Object> map = (HashMap) jsonObject;
                map.keySet().forEach(key -> {
                    var element = (HashMap) map.get(key);
                    State state = constructState(element);
                    stateMap.put(key, state);
                });
            });
            return stateMap;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}