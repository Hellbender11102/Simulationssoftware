package model;

import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.PhysicalEntity;

import java.awt.*;
import java.util.Random;

//TODO,0
public class Wall extends BasePhysicalEntity {
    protected Wall(Arena arena, double width, double height) {
        super(arena, null, width, height);
    }


    @Override
    public boolean hasAnBody() {
        return false;
    }

    @Override
    public double trajectorySpeed() {
        return 0;
    }

    @Override
    public Color getClassColor() {
        return new Color(55,55,55);
    }

    @Override
    public boolean isPositionInEntity(Position position) {
        return position.getXCoordinate() <= pose.getXCoordinate() + height / 2 &&
                position.getXCoordinate() >= pose.getXCoordinate() - height / 2 &&
                position.getYCoordinate() <= pose.getYCoordinate() + width / 2 &&
                position.getYCoordinate() >= pose.getYCoordinate() - width / 2;
    }
    @Override
    public Position getClosestPositionInBody(Position position) {
        return null;
    }

    @Override
    public int getTimeToSimulate() {
        return 0;
    }
}
