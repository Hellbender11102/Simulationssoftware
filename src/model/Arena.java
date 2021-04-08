package model;

import java.util.LinkedList;

 public class Arena {
    private LinkedList<Robot> robotList;
    private final int height, width;

    /**
     * Constructor
     *
     * @param width  in centemeter
     * @param height in centemeter
     */
    public Arena(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "width:" + width + " height:" + height;
    }

   synchronized public void setRobots(LinkedList<Robot> robotList) {
        this.robotList = robotList;
    }
  synchronized  public LinkedList<Robot> getRobots() {
      return robotList;
    }

 synchronized   public int getHeight() {
        return height;
    }

  synchronized  public int getWidth() {
        return width;
    }
}
