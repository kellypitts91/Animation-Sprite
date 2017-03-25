
//Kelly Pitts 09098321
import java.awt.image.BufferedImage;

class DrawDying {
    private BufferedImage[][] frames;
    private BufferedImage sprite;
    private long previousTime = 0;
    private int currentFrame = 0;
    private boolean animationState = false;

    DrawDying(BufferedImage[][] frames) {
        this.frames = frames;
    }

    void setAnimationState(boolean animationState) {
        this.animationState = animationState;
    }

    void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    void update(long time) {
        if(time - previousTime >= 200) {
            //Update animation
            currentFrame++;
            sprite = frames[3][currentFrame];
            //end of frame, change animation state but don't reset to 0 until user presses "UP" in main program
            if(currentFrame == 8) {
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
