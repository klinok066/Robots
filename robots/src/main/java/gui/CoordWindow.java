package gui;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.beans.PropertyVetoException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import model.ModelRobot;
import serialization.Saveable;
import serialization.State;

/**
 * Класс CoordWindow представляет внутреннее окно, отображающее координаты робота.
 * Реализует интерфейсы Saveable и Observer.
 */
public class CoordWindow extends JInternalFrame implements Saveable, Observer
{
    private final TextArea textArea;
    private final ModelRobot modelRobot;

    /**
     * Конструктор класса CoordWindow.
     * @param modelRobot Модель робота
     */
    public CoordWindow(ModelRobot modelRobot)
    {
        super("Координаты робота", true, true, true, true);
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setVisible(true);
        this.modelRobot = modelRobot;
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    /**
     * Метод обновления окна при изменении состояния модели.
     * @param o Наблюдаемый объект (модель робота)
     * @param arg Аргумент (состояние модели)
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o == null) {
            return;
        }

        if (modelRobot.equals(o)) {
            if (ModelRobot.key_robot_pos_changed.equals(arg)) {
                onRobotPositionChanged();
            }
        }
    }

    /**
     * Метод обработки изменения позиции робота.
     * Обновляет текстовую область с новыми координатами робота.
     */
    private void onRobotPositionChanged() {

        String coords = "x: " + ((int)modelRobot.getRobotX()) +
                ", y: " + ((int)modelRobot.getRobotY() +
                ", direction: " + ((int) (modelRobot.getDirection() * 180 / Math.PI)));
        textArea.setText(coords);
    }
    @Override
    public State getState() {
        State state = new State();
        state.setProperty("name", this.getName());
        state.setProperty("height", this.getSize().height);
        state.setProperty("width", this.getSize().width);
        state.setProperty("location_x", this.getLocation().getX());
        state.setProperty("location_y", this.getLocation().getY());
        state.setProperty("is_hidden", this.isIcon);
        return state;
    }

    @Override
    public void loadState(State state) {
        if (null == state) {
            return;
        }
        Object height = state.getProperty("height");
        Object width = state.getProperty("width");
        long locationX = Math.round((Double) state.getProperty("location_x"));
        long locationY = Math.round((Double) state.getProperty("location_y"));
        Object isHidden = state.getProperty("is_hidden");

        this.setSize(Math.toIntExact((Long) width), Math.toIntExact((Long) height));
        this.setLocation(Math.toIntExact(locationX), Math.toIntExact(locationY));

        try {
            this.setIcon((Boolean) isHidden);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "CoordWindow";
    }
}