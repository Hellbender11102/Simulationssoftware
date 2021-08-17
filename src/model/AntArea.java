package model;

import model.abstractModel.BaseEntity;

import java.awt.*;
import java.util.Random;

/**
 * This can be used to represent Light sources or food sources
 */
public class AntArea extends Area {

    double noticeableDistanceDiameters;

    public AntArea(Arena arena, Random random, double diameters, double noticeableDistanceDiameters, Pose pose) {
        super(arena, random, diameters, diameters, pose);
        this.noticeableDistanceDiameters = noticeableDistanceDiameters;
    }

    /**
     * Returns the color for the Class Area
     *
     * @return Color
     */
    @Override
    public Color getClassColor() {
        return new Color(90, 10, 120);
    }


    @Override
    public String toString() {
        return "AntArea at " + pose + " radius:" + width / 2;
    }
}
