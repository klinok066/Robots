package gui;

import model.ModelRobot;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 * Класс GameVisualizer представляет панель JPanel, которая отображает визуальное представление робота.
 * Он наблюдает за классом ModelRobot, и обновляет отображение.
 */
public class GameVisualizer extends JPanel implements Observer
{
    private final ModelRobot modelRobot;
    private final int duration;

    /**
     * Конструктор класса GameVisualizer.
     * @param modelRobot Модель робота, за которой следит визуализатор.
     * @param duration Длительность перемещения робота.
     */
    public GameVisualizer(ModelRobot modelRobot, int duration)
    {
        this.modelRobot = modelRobot;
        this.duration = duration;
        setDoubleBuffered(true);
    }

    /**
     * Метод, вызываемый при событии перерисовки, инициирует перерисовку панели.
     */
    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    /**
     * Метод, вызываемый при событии обновления модели робота, вызывает метод перемещения робота на новые координаты.
     */
    protected void onModelUpdateEvent()
    {
        modelRobot.moveRobot(modelRobot.getTargetX(), modelRobot.getTargetY(), duration);
    }
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        drawRobot(g2d, round(modelRobot.getRobotX()), round(modelRobot.getRobotY()), modelRobot.getDirection());
        drawTarget(g2d, modelRobot.getTargetX(), modelRobot.getTargetY());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = round(modelRobot.getRobotX());
        int robotCenterY = round(modelRobot.getRobotY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private void onRobotPositionChanged() {
        onRedrawEvent();
    }

    private void onTargetPositionChanged() {
        onRedrawEvent();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (modelRobot.equals(o)) {
            if (ModelRobot.key_robot_pos_changed.equals(arg)) {
                onRobotPositionChanged();
            }
        }
        if (modelRobot.equals(o)) {
            if (ModelRobot.key_target_pos_changed.equals(arg)) {
                onTargetPositionChanged();
            }
        }
    }
}