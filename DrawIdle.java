
//Kelly Pitts 09098321
import java.awt.image.BufferedImage;

class DrawIdle {
    private BufferedImage[][] frames;
    private BufferedImage sprite;
    private long previousTime = 0;
    private int currentFrame = 0;

    DrawIdle(BufferedImage[][] frames) {
        this.frames = frames;
    }

    void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    void update(long time) {
        if(time - previousTime >= 200) {
            //Update animation
            currentFrame++;
            sprite = frames[1][currentFrame];
            //end of frame, reset to 0
            if(currentFrame == 6) {
                currentFrame = 0;
            }
            previousTime = time;
        }
    }

    BufferedImage getSprite() {
        return sprite;
    }
}
