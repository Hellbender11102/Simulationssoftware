package model;

import model.AbstractModel.BaseEntity;

import java.awt.*;
import java.util.Random;

/**
 * This can be used to represent Light sources or food sources
 */
//TODO Arena
//TODO
public class Area extends BaseEntity {

    double noticeableDistanceDiameters;

    public Area(Arena arena, Random random, double diameters, double noticeableDistanceDiameters, Pose pose) {
        super(arena, random, diameters, diameters, pose);
        this.noticeableDistanceDiameters = noticeableDistanceDiameters;
    }

    public void decreaseArea(double number) {
        height += number / 2;
        width += number / 2;
    }

    public void decreaseAreaOfSight(double number) {
        noticeableDistanceDiameters -= number;
    }

    public void increaseArea(double number) {
        height += number / 2;
        width += number / 2;
    }

    public void increaseAreaOfSight(double number) {
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
        return noticeableDistanceDiameters /2;
    }

    public Position getClosestPositionInEntity(Position position) {
        return closestPositionInEntityForCircle(position, width / 2.);
    }

    @Override
    public boolean isCollidable() {
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


}
