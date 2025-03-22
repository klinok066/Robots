package gui;

import serialization.Saveable;
import serialization.State;
import model.ModelRobot;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements Saveable
{
    private final GameVisualizer m_visualizer;
    private final ModelRobot modelRobot;
    private final CoordWindow coordWindow;

    private final Timer m_timer = initTimer();
    private final static int durationRedraw = 10;

    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }
    public GameWindow(ModelRobot robot)
    {
        super("Игровое поле", true, true, true, true);
        modelRobot = robot;
        m_visualizer = new GameVisualizer(modelRobot, durationRedraw);
        coordWindow = new CoordWindow(modelRobot);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                m_visualizer.onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                m_visualizer.onModelUpdateEvent();
            }
        }, 0, durationRedraw);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                modelRobot.setTargetPosition(new Point(
                        e.getX() - (getWidth() - m_visualizer.getWidth()) / 2,
                        e.getY() + (getHeight() - m_visualizer.getHeight() - coordWindow.getHeight()) / 4
                                - coordWindow.getHeight()
                                - (getHeight() - m_visualizer.getHeight() - coordWindow.getHeight()))
                );
                repaint();
            }
        });

        modelRobot.addObserver(coordWindow);
        modelRobot.addObserver(m_visualizer);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordWindow, BorderLayout.WEST);
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