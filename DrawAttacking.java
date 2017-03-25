
//Kelly Pitts 09098321
import java.awt.image.BufferedImage;

class DrawAttacking {
    private BufferedImage[][] frames;
    private BufferedImage sprite;
    private long previousTime = 0;
    private int currentFrame = 0;
    private boolean animationState = false;

    DrawAttacking(BufferedImage[][] frames) {
        this.frames = frames;
    }

    void setAnimationState(boolean animationState) {
        this.animationState = animationState;
    }

    void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    void update(long time) {
        if(time - previousTime >= 200) {
            //Update animation
            currentFrame++;
            sprite = frames[4][currentFrame];
            //end of frame, reset to 0
            if(currentFrame == 8) {
                currentFrame = 0;
                animationState = true;
            }
            previousTime = time;
        }
    }

    BufferedImage getSprite() {
        return sprite;
    }

    boolean getAnnimationState() {
        return animationState;
    }
}
