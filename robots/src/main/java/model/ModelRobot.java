package model;

import java.awt.*;
import java.util.Observable;

/**
 * Основной класс модели робота, который представляет робота и его перемещение к цели.
 * Реализует интерфейс Observer, чтобы следить за изменениями позиции цели и оповещать об этом наблюдателей.
 */
public class ModelRobot extends Observable {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    public static final String key_robot_pos_changed = "robot position changed";
    public static final String key_target_pos_changed = "target position changed";

    /**
     * Устанавливает новую позицию цели.
     *
     * @param p новая позиция цели
     */
    public void setTargetPosition(Point p)
    {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;

        setChanged();
        notifyObservers();
        clearChanged();
    }

    /**
     * Возвращает текущую позицию точки по оси X.
     *
     * @return текущая позиция точки по оси X
     */
    public int getTargetX() {
        return m_targetPositionX;
    }

    /**
     * Возвращает текущую позицию точки по оси Y.
     *
     * @return текущая позиция точки по оси Y
     */
    public int getTargetY() {
        return m_targetPositionY;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    public void moveRobot(double m_targetPositionX, double m_targetPositionY, double duration)
    {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        double angle = asNormalizedRadians(angleToTarget - m_robotDirection);

        if (angle < Math.PI / 2) {
            angularVelocity = maxAngularVelocity;
        } else if (angle > Math.PI / 2) {
            angularVelocity = -maxAngularVelocity;
        }
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);

        setChanged();
        notifyObservers(key_robot_pos_changed);
        clearChanged();
    }

    /**
     * Возвращает текущую позицию робота по оси X.
     *
     * @return текущая позиция робота по оси X
     */
    public double getRobotX() {
        return m_robotPositionX;
    }

    /**
     * Возвращает текущую позицию робота по оси Y.
     *
     * @return текущая позиция робота по оси Y
     */
    public double getRobotY() {
        return m_robotPositionY;
    }

    /**
     * Возвращает текущий угол направления робота в радианах.
     *
     * @return текущий угол направления робота в радианах
     */
    public double getDirection() {
        return m_robotDirection;
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

}