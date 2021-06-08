package model;

import model.AbstractModel.BaseEntity;
import model.AbstractModel.PhysicalEntity;

import java.awt.*;
import java.util.Random;

/**
 * This can be used to represent Light sources or food sources
 */

//TODO
public class Area extends BaseEntity {

    protected Area(Arena arena, Random random, double width, double height) {
        super(arena, random, width, height);
    }

    @Override
    public boolean equals(PhysicalEntity robot) {
        return false;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public boolean draw(Graphics g) {
        return false;
    }
}
