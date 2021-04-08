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
}
