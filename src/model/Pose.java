package model;

public class Pose extends Position {
    double rotation;

    public Pose(double xCoordinate, double yCoordinate, double rotation) {
        super(xCoordinate, yCoordinate);
        this.rotation = rotation % 360;
    }

    public void setRotation(double rotation) {
        if (rotation < 0) rotation += 360;
        if (rotation >= 360) this.rotation = rotation % 360;
        else this.rotation = rotation;
    }

    public void incRotation(double rotation) {
        if (rotation < 0) rotation += 360;
        if (this.rotation + rotation >= 360) this.rotation = (this.rotation + rotation) % 360;
        else this.rotation = this.rotation + rotation % 360;
    }

    public double getRotation() {
        return rotation;
    }

    public boolean equals(Pose position) {
        return super.equals(this) && rotation == position.rotation;
    }

    public Position getPositionInDirection(double distance) {
        double x = 0, y = 0;
        double small = rotation % 90;
        if (rotation == 90) {
            x = getxCoordinate();
            y = getyCoordinate() + distance;
        } else if (rotation == 180) {
            x = getxCoordinate() - distance;
            y = getyCoordinate();
        } else if (rotation == 270) {
            x = getxCoordinate();
            y = getyCoordinate() - distance;
        } else if (rotation == 0) {
            x = getxCoordinate() + distance;
            y = getyCoordinate();
        } else if (rotation < 90.0) {
            x = getxCoordinate() + (distance * (1 - small / 90));
            y = getyCoordinate() + (distance * (small / 90));
        } else if (rotation < 180.0) {
            x = getxCoordinate() - (distance * (small / 90));
            y = getyCoordinate() + (distance * (1 - small / 90));
        } else if (rotation < 270.0) {
            x = getxCoordinate() - (distance * (1 - small / 90));
            y = getyCoordinate() - (distance * (small / 90));
        } else if (rotation < 360.0) {
            x = getxCoordinate() + (distance * (small / 90));
            y = getyCoordinate() - (distance * (1 - small / 90));
        }
        return new Position(x, y);
    }

}