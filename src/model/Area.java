package model;

import model.abstractModel.BaseEntity;

import java.awt.*;
import java.util.Random;

/**
 * This can be used to represent Light sources or food sources
 */
public class Area extends BaseEntity {

    double noticeableDistanceDiameters;

    public Area(Arena arena, Random random, double diameters, double noticeableDistanceDiameters, Pose pose) {
        super(arena, random, diameters, diameters, pose);
        this.noticeableDistanceDiameters = noticeableDistanceDiameters;
    }

    /**
     * Decreases the size of the area
     *
     * @param number double
     */
    public void decreaseAreaDiameters(double number) {
        if (getDiameters() >= number) {
            height -= number;
            width -= number;
        } else {
            height = 0;
            width = 0;
        }
    }

    /**
     * Decreases the size of the area
     *
     * @param number double
     */
    public void decreaseNoticeableDistanceDiameters(double number) {
        if (noticeableDistanceDiameters >= number) {
            noticeableDistanceDiameters -= number;
        } else {
            noticeableDistanceDiameters = 0;
        }
    }

    /**
     * Increases the size of the area
     *
     * @param number double
     */
    public void increaseArea(double number) {
        height += number;
        width += number;
    }

    /**
     * Increases the noticeable distance
     *
     * @param number double
     */
    public void increaseNoticeableDistanceDiameters(double number) {
        noticeableDistanceDiameters += number;
    }

    /**
     * Calculates if position is in Area radius
     *
     * @param position Position
     * @return boolean
     */
    public boolean isPositionInEntity(Position position) {
        return isPositionInEntityCircle(position);
    }

    /**
     * Returns the color for the Class Area
     *
     * @return Color
     */
    @Override
    public Color getClassColor() {
        return new Color(10, 180, 120);
    }

    /**
     * Returns the noticeable distance of the Area which can be smaller than the area itself
     *
     * @return double
     */
    public double getNoticeableDistanceDiameter() {
        return noticeableDistanceDiameters;
    }

    /**
     * Returns the noticeable distance of the Area which can be smaller than the area itself
     *
     * @return double
     */
    public double getNoticeableDistanceRadius() {
        return noticeableDistanceDiameters / 2;
    }

    public Position getClosestPositionInEntity(Position position) {
        if (pose.getEuclideanDistance(position) < getRadius()) return position;
        return closestPositionInEntityForCircle(position, getRadius());
    }

    @Override
    public boolean hasPhysicalBody() {
        return false;
    }

    @Override
    public double getArea() {
        return getAreaCircle();
    }

    public double getDiameters() {
        return width;
    }

    public double getRadius() {
        return width / 2;
    }

    @Override
    public String toString() {
        return "Area at " + pose + " radius:" + width / 2;
    }
}
