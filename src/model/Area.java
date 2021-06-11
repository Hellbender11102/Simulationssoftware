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

    double noticeableDistance;

    public Area(Arena arena, Random random, double diameters, double noticeableDistance, Pose pose) {
        super(arena, random, diameters, diameters, pose);
        this.noticeableDistance = noticeableDistance;
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

    @Override
    public Color getClassColor() {
        return new Color(180, 255, 180);
    }

    public double getNoticeableDistance() {
        return noticeableDistance;
    }
}
