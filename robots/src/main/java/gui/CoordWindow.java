package gui;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import serialization.Saveable;
import serialization.State;

public class CoordWindow extends JInternalFrame implements Saveable
{
    private TextArea textArea;

    public CoordWindow()
    {
        super("Координаты робота", true, true, true, true);
        textArea = new TextArea();
        textArea.setText("test1");
        textArea.setText("test2");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
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
        return "CoordWindow";
    }
}