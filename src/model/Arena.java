package model;

public class Arena {
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    final int height, width;

    /**
     * Constructor
     * @param width in centemeter
     * @param height in centemeter
     */
   public Arena(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "width:"+width +" height:" +height;
    }
}
