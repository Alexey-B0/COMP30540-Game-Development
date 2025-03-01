package util;

public class PlayerObject extends GameObject {
    private boolean hit;
    private long timeOfHit;
    
    public PlayerObject(String textureLocation,int width,int height,Point3f centre) {
        super(textureLocation, width, height, centre);
        this.hit = false;
    }

    public boolean isHit() {
        return this.hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public long getTimeOfHit() {
        return timeOfHit;
    }

    public void setTimeOfHit(long timeOfHit) {
        this.timeOfHit = timeOfHit;
    }

    
}
