package model;

public class Vector {

    private Position origin, destination;
    private double length, polarAngle;

    public Vector(Position origin, Position destination) {
        this.origin = origin;
        this.destination = destination;
        length = Math.hypot(destination.getXCoordinate() - origin.getXCoordinate(),
                destination.getYCoordinate() - origin.getYCoordinate());
        polarAngle = Math.toDegrees(Math.atan2(destination.getXCoordinate() - origin.getXCoordinate(),
                destination.getYCoordinate() - origin.getYCoordinate()));
    }

    public Vector(Position destination) {
        origin = new Position(0, 0);
        this.destination = destination;
        length = Math.hypot(destination.getXCoordinate(), destination.getYCoordinate());
        polarAngle = Math.toDegrees(Math.atan2(destination.getXCoordinate(),
                destination.getYCoordinate()));
    }

    public Vector(Position origin,double length, double polarAngle){
        this.origin = origin;
        this.length = length;
        this.polarAngle = polarAngle;
        this.destination = new Pose(origin,polarAngle).getPositionInDirection(length);
    }
}
