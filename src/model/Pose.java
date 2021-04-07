package model;

public class Pose extends Position {
    double rotation;
    public Pose(double xCoordinate, double yCoordinate, double rotation) {
        super(xCoordinate, yCoordinate);
        this.rotation = rotation % 360;
    }
        public void setRotation(double rotation) {
        while (rotation < 0) rotation += 360;
        this.rotation = rotation % 360;
    }
        public void incRotation(double rotation) {
        while (rotation < 0) rotation += 360;
        this.rotation = rotation + rotation % 360;
    }
    public double getRotation() {
        return rotation;
    }

    public boolean equals(Pose position) {
        return super.equals(this) && rotation == position.rotation;
    }
}
