package model.AbstractModel;

public interface RobotInterface extends Runnable, PhysicalEntity {

    abstract void behavior();

    abstract public double getDiameters();

    abstract public double getRadius();

    abstract public double getEngineL();

    abstract public double getEngineR();
}