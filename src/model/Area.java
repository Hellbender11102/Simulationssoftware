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

    double noticeableDistance =0;

    protected Area(Arena arena, Random random, double width, double height) {
        super(arena, random, width, height);
    }

    @Override
    public boolean hasAnBody() {
        return false;
    }

     /**
     * Calculates if position is in Area radius
     *
     * @param position Position
     * @return boolean
     */
    public boolean isPositionInEntity(Position position) {
        return pose.euclideanDistance(position) <= width;
    }


}
