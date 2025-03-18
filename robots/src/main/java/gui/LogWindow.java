package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.beans.PropertyVetoException;


import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import serialization.Saveable;
import serialization.State;

public class LogWindow extends JInternalFrame implements LogChangeListener, Saveable
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource)
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
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
        return "LogWindow";
    }
}