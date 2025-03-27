package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import log.Logger;
import model.ModelRobot;
import serialization.Saveable;
import serialization.State;
import serialization.StateHandler;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    private final String path = (System.getProperty("user.home") + "/state.json");
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);
        StateHandler stateHandler = new StateHandler(path);
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        ModelRobot robot = new ModelRobot();

        GameWindow gameWindow = new GameWindow(robot);
        addWindow(gameWindow);

        CoordWindow coordWindow = new CoordWindow(robot);
        robot.addObserver(coordWindow);
        addWindow(coordWindow);

        Map<String, State> states = stateHandler.loadAllData();
        File stateFile = new File(path);
        if (stateFile.exists()) {
            stateHandler = new StateHandler(path);
            states = stateHandler.loadAllData();
            logWindow.loadState(states.get("LogWindow"));
            gameWindow.loadState(states.get("GameWindow"));
            coordWindow.loadState(states.get("CoordWindow"));
        } else {
            states = getDefaultStates();
        }

        UIManager.put("OptionPane.yesButtonText","Да");
        UIManager.put("OptionPane.noButtonText","Нет");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setClose();
            }
        });
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private Map<String, State> getDefaultStates() {
        State logWindowState = new State();
        logWindowState.setProperty("x", 10);
        logWindowState.setProperty("y", 10);
        logWindowState.setProperty("width", 300);
        logWindowState.setProperty("height", 800);

        State gameWindowState = new State();
        gameWindowState.setProperty("x", 0);
        gameWindowState.setProperty("y", 0);
        gameWindowState.setProperty("width", 400);
        gameWindowState.setProperty("height", 400);

        State coordWindowState = new State();
        coordWindowState.setProperty("x", 0);
        coordWindowState.setProperty("y", 0);
        coordWindowState.setProperty("width", 400);
        coordWindowState.setProperty("height", 400);

        State defaultStates = new State();
        defaultStates.setProperty("LogWindow", logWindowState);
        defaultStates.setProperty("GameWindow", gameWindowState);
        defaultStates.setProperty("CoordWindow", coordWindowState);

        return Map.of("LogWindow", logWindowState, "GameWindow", gameWindowState, "CoordWindow", coordWindowState);
    }
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    //    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }
    private void setClose() {
        int result = JOptionPane.showConfirmDialog(MainApplicationFrame.this,
                "Вы уверены?",
                "Выход",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            StateHandler stateHandler = new StateHandler(path);
            List<Saveable> saveableObjects = new ArrayList<>();

            for (final JInternalFrame frame : desktopPane.getAllFrames()) {
                if (frame instanceof Saveable) {
                    saveableObjects.add((Saveable) frame);
                }
            }
            stateHandler.save(saveableObjects);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        JMenu closeMenu = new JMenu("Закрыть");
        closeMenu.setMnemonic(KeyEvent.VK_C);
        closeMenu.getAccessibleContext().setAccessibleDescription(
                "Закрытие приложения");

        {
            JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X | KeyEvent.VK_ALT);
            exitItem.addActionListener((event) -> {
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                        new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            });
            closeMenu.add(exitItem);
        }

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(closeMenu);
        return menuBar;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

}