package org.klinok066.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import org.klinok066.log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    /**
     * Конструктор, создающий окнаБ генерирует меню
     */
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             * Обработчик метода для выхода из приложения
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                Exit(e);
            }
        });

    }

    /**
     * Слушатель обработчика windowClosing
     * @param event
     */
    private void Exit(WindowEvent event) {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        Object[] options = {"Да", "Нет"};

        int response = JOptionPane.showOptionDialog(
                event.getWindow(),
                "Закрыть приложение?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (response == 0) {
            event.getWindow().setVisible(false);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(event.getWindow(), WindowEvent.WINDOW_CLOSING)
            );
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
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
//
//        //Set up the first menu item.
//
//        JMenuItem menuItem = new JMenuItem("New");
//        JMenuBar menuBar = new JMenuBar();
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//
//        //Set up the second menu item.
//
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
//
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//        return menuBar;
//    }

    /**
     * @param title - Название
     * @param button - бинд клавиши
     * @param descriptionText - описание кнопки
     * @return - возвращает контекстное меню
     */
    private JMenu createMenu(String title, int button, String descriptionText){
        JMenu menu = new JMenu(title);
        menu.setMnemonic(button);
        menu.getAccessibleContext().setAccessibleDescription(descriptionText);

        return menu;
    }

    /**
     * метод создаёт элемент для меню
     * @param text - название элемента
     * @param keyEvent - бинд клавиши
     * @param listener - слушатель
     * @return - возвращает элемент для меню
     */
    private JMenuItem createMenuItem(String text, int keyEvent, ActionListener listener){
        JMenuItem item = new JMenuItem( text, keyEvent);
        item.addActionListener(listener);
        return item;
    }

    /**
     * метод отвечает за создание и настройка меню и его элементов
     */
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = createMenu("Режим отображения", KeyEvent.VK_V,"Управление режимом отображения приложения");

        lookAndFeelMenu.add(createMenuItem("Системная схема", KeyEvent.VK_S,(event) ->{
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        }));

        lookAndFeelMenu.add(createMenuItem("Универсальная схема",KeyEvent.VK_S, (event) ->{
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        }));

        JMenu testMenu = createMenu("Тесты", KeyEvent.VK_T, "Тестовые команды");

        testMenu.add(createMenuItem("Сообщение в лог", KeyEvent.VK_S, (event) ->{
            Logger.debug("Новая строка");
        }));
        JMenuItem  closeMenu = createMenu("Закрыть", KeyEvent.VK_C,"Закрытие приложения");
        closeMenu.add(createMenuItem("Выход",KeyEvent.VK_S,(event)->{
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }));

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
