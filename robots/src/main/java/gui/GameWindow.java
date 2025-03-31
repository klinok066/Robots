package gui;

import serialization.Saveable;
import serialization.State;

import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements Saveable
{
    private final GameVisualizer m_visualizer;
    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
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
        return "GameWindow";
    }
}