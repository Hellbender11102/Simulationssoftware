package model;

import model.AbstractModel.BasePhysicalEntity;
import model.AbstractModel.PhysicalEntity;

import java.awt.*;
import java.util.Random;

//TODO
public class Box extends BasePhysicalEntity {
    Position edgeUL, edgeUR, edgeLL, edgeLR;
    public Box(Arena arena, Random random, double width, double height, Pose pose) {
        super(arena, random, width, height,pose);
                edgeUL = new Position(pose.getXCoordinate() - width / 2, pose.getYCoordinate() + height / 2);
        edgeUR = new Position(pose.getXCoordinate() + width / 2, pose.getYCoordinate() + height / 2);
        edgeLL = new Position(pose.getXCoordinate() - width / 2, pose.getYCoordinate() - height / 2);
        edgeLR = new Position(pose.getXCoordinate() + width / 2, pose.getYCoordinate() - height / 2);
    }

    @Override
    public double trajectorySpeed() {
        return 0;
    }

    @Override
    public Color getClassColor() {
        return new Color(50, 32, 28);
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
        Position closest =
                Math.min(edgeUL.euclideanDistance(position), edgeUR.euclideanDistance(position)) >
                        Math.min(edgeLL.euclideanDistance(position), edgeLR.euclideanDistance(position)) ?
                        edgeLL.euclideanDistance(position) > edgeLR.euclideanDistance(position) ? edgeLR : edgeLL
                        :
                        edgeUL.euclideanDistance(position) > edgeUR.euclideanDistance(position) ? edgeUR : edgeUL;
      if(position.getXCoordinate() <= pose.getXCoordinate() + width / 2 &&
              position.getXCoordinate() >= pose.getXCoordinate() - width / 2){
          closest.setXCoordinate(position.getXCoordinate());
      } else if(position.getYCoordinate() <= pose.getYCoordinate() + height / 2 &&
                position.getYCoordinate() >= pose.getYCoordinate() - height / 2){
                 closest.setYCoordinate(position.getYCoordinate());
      }
      return closest;
    }

    @Override
    public int getTimeToSimulate() {
        return 0;
    }

}
