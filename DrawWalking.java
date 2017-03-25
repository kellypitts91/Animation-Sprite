
//Kelly Pitts 09098321
import java.awt.image.BufferedImage;

class DrawWalking {
    private BufferedImage[][] frames;
    private BufferedImage sprite;
    private long previousTime = 0;
    private int currentFrame = 0;

    DrawWalking(BufferedImage[][] frames) {
        this.frames = frames;
    }

    void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    void update(long time) {
        if(time - previousTime >= 200) {
            //Update animation
            currentFrame++;
            sprite = frames[2][currentFrame];
            //end of frame, reset to 0
            if(currentFrame == 8) {
                currentFrame = 0;
            }
            previousTime = time;
        }
    }

    BufferedImage getSprite() {
        return sprite;
    }
}
