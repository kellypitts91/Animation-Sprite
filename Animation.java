
//Kelly Pitts 09098321
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Animation extends JPanel implements KeyListener, Runnable {

    private static final long serialVersionUID = 1L;
    private BufferedImage spriteSheet;
    private BufferedImage[][] sprites = new BufferedImage[5][50];
    private int positionX = 0, positionY = 0;
    private static int keyPressCount = 0;
    private static String keyPressed = "";
    private static boolean rightPressed = false, leftPressed = false, rightFacing = false;
    private static boolean downPressed = false, idle = false, firstLoad = true;

    private DrawIdle drawIdle;
    private DrawUp drawUp;
    private DrawDying drawDying;
    private DrawWalking drawWalking;
    private DrawAttacking drawAttack;

    private Animation() {
        final JFrame frame = new JFrame("Animation");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // set up the content pane
        frame.setContentPane(this);
        frame.setSize(1100, 700);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.setBackground(Color.WHITE);

        positionX = (frame.getWidth() / 2) - (256 / 2); //middle of screen
        positionY = (frame.getHeight() / 2) + 50;       //bottom, middle of screen
        frame.addKeyListener(this);

        try {
            //reading file to BufferedImage variable
            spriteSheet = ImageIO.read(new File("skeleton-sprite.png"));
            //creating a 2D array of the different animations
            //BufferedImage[][] sprites = new BufferedImage[5][50];
            int size = 256; //size of image
            int[] numberOfColumns = new int[]{10, 6, 8, 8, 8}; //number of sprites for each line.
            //loads sprites into a 2D array, 1 action per array
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < numberOfColumns[i]; j++) {
                    sprites[i][j] = spriteSheet.getSubimage(j * size, i * size, size, size);
                }
            }

            //creating classes for each different animation.
            drawUp = new DrawUp(sprites);
            drawIdle = new DrawIdle(sprites);
            drawWalking = new DrawWalking(sprites);
            drawDying = new DrawDying(sprites);
            drawAttack = new DrawAttacking(sprites);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread loop = new Thread(this);
        loop.start();
    }

    public void run() {
        // Get a reference to the current thread
        Thread th = Thread.currentThread();
        // Do this block of code until the thread gets interrupted
        while (!th.isInterrupted()) {
            myUpdate();
            try {
                // Pause here
                Thread.sleep(200);
            } catch (InterruptedException e) {
                th.interrupt(); // interrupted flag was cleared, set it again
            }
        }
    }

    private void myUpdate() {
        //checking which key has been pressed
        //Idle animation wont happen until the current animation has finished
        //stops animation going in a loop
        switch (keyPressed) {
            case "UP":
                idle = drawUp.getAnimationState();
                break;
            case "LEFT":
            case "RIGHT":
                idle = false;
                break;
            case "SPACE":
                idle = drawAttack.getAnnimationState();
                break;
        }
        //if the animation is not idle from the above switch statement
        //check the following
        if(!idle) {
            //checking which animation to draw
            switch (keyPressed) {
                case "UP":
                    if(keyPressCount == 1) {
                        drawUp.update(System.currentTimeMillis());
                    }
                    break;
                case "DOWN":
                    //checking if the draw animation is in the last state
                    //if so will stay in the last frame until user presses up
                    if(drawDying.getAnnimationState()) {
                        drawDying.setCurrentFrame(8);
                    }
                    drawDying.update(System.currentTimeMillis());
                    break;
                case "LEFT":
                    //checking left arrow held and not in dead position
                    //otherwise exit the function and don't update the paint component
                    if (leftPressed && !downPressed) {
                        drawWalking.update(System.currentTimeMillis());
                    } else {
                        return;
                    }
                    break;
                case "RIGHT":
                    //checking right arrow held and not in dead position
                    //otherwise exit the function and don't update the paint component
                    if (rightPressed && !downPressed) {
                        drawWalking.update(System.currentTimeMillis());
                    } else {
                        return;
                    }
                    break;
                case "SPACE":
                    drawAttack.update(System.currentTimeMillis());
                    break;
                case "IDLE":
                    drawIdle.update(System.currentTimeMillis());
                    break;
            }
        } else {
            drawIdle.update(System.currentTimeMillis());
        }
        //update paint component
        this.repaint();
    }

    public void keyPressed(KeyEvent kev) {
        if (kev.getKeyCode() == KeyEvent.VK_UP) {
            //keyPressCount keeps track of user pressing any key that is not the up key.
            //to ensure the up animation only happens when user is either at start of application or in the dead position
            if (keyPressCount > 0) {
                keyPressed = "IDLE";
                idle = true;
            } else {
                keyPressed = "UP";
                idle = false;
            }
            downPressed = false;
            drawUp.setAnimationState(false);
            //updating dying animation to start back at first frame next time user presses down.
            drawDying.setCurrentFrame(0);
        } else if (kev.getKeyCode() == KeyEvent.VK_DOWN) {
            //only time animation can rise is if in dead position or at start of program.
            keyPressCount = 0;
            keyPressed = "DOWN";
            downPressed = true;
        } else if (kev.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
            if (!downPressed) {
                keyPressed = "LEFT";
            }
        } else if (kev.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
            if (!downPressed) {
                keyPressed = "RIGHT";
            }
        } else if (kev.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!downPressed) {
                keyPressed = "SPACE";
            }
        }
        if (!keyPressed.equals("DOWN")) {
            keyPressCount++;
        }
    }

    public void keyReleased(KeyEvent kev) {

        if (kev.getKeyCode() == KeyEvent.VK_DOWN) {
            drawDying.setAnimationState(false);
            idle = false;
        } else if (kev.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
            //user can press right and left while in dead position
            //this determines which way to rise
            if(keyPressCount != 0) {
                rightFacing = false;
            }
            idle = animationState();
        } else if (kev.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
            //determines which way to rise
            if(keyPressCount != 0) {
                rightFacing = true;
            }
            idle = animationState();
        } else if (kev.getKeyCode() == KeyEvent.VK_SPACE) {
            drawAttack.setAnimationState(false);
        }
    }
    //checking if in dead position, if not idle animation will play.
    private boolean animationState() {
        if(downPressed) {
            keyPressed = "DOWN";
            return false;
        }
        keyPressed = "IDLE";
        return true;
    }

    //press q to quit program
    public void keyTyped(KeyEvent kev) {
        if (kev.getKeyChar() == 'q' || kev.getKeyChar() == 'Q') { // quit
            System.exit(0);
        }
    }

   @Override
   public void paintComponent(Graphics g) {
       Graphics2D g2 = (Graphics2D) g;
       if(firstLoad) {
           g2.drawImage(sprites[0][0], positionX, positionY, null);
           firstLoad = false;
       }
       //making sure the file is not null before continuing
       if (spriteSheet != null) {
           //determines which animation to draw based off earlier checks
           switch (keyPressed) {
               case "UP":
                   appear(g2);
                   break;
               case "DOWN":
                   die(g2);
                   break;
               case "LEFT":
                   walkLeft(g2);
                   break;
               case "RIGHT":
                   walkRight(g2);
                   break;
               case "SPACE":
                   attack(g2);
                   break;
           }
           if(idle) {
               idle(g2);
           }
       }
   }

    private void appear(Graphics2D g2) {
        //checking sprite is not null
        if(drawUp.getSprite() != null) {
            //clearing the image so it does not show previous frames
            g2.clearRect(0, 0, getWidth(), getHeight());
            //determining direction of animation
            if(rightFacing) {
                drawUp.setSprite(faceRight(drawUp.getSprite()));
            }
            g2.drawImage(drawUp.getSprite(), positionX, positionY, null);
        }
    }

    private void walkLeft(Graphics2D g2) {
        //checking sprite is not null
        if(drawWalking.getSprite() != null) {
            positionX -= 10;
            //clearing the image so it does not show previous frames
            g2.clearRect(0, 0, getWidth(), getHeight());
            g2.drawImage(drawWalking.getSprite(), positionX, positionY, null);
        }
    }

    private void walkRight(Graphics2D g2) {
        //checking sprite is not null
        if(drawWalking.getSprite() != null) {
            positionX += 10;
            //clearing the image so it does not show previous frames
            g2.clearRect(0, 0, getWidth(), getHeight());
            drawWalking.setSprite(faceRight(drawWalking.getSprite()));
            g2.drawImage(drawWalking.getSprite(), positionX, positionY, null);
        }
    }

    private void die(Graphics2D g2) {
        //checking sprite is not null
        if(drawDying.getSprite() != null) {
            //clearing the image so it does not show previous frames
            g2.clearRect(0, 0, getWidth(), getHeight());
            //determining direction of animation
            if(rightFacing) {
                drawDying.setSprite(faceRight(drawDying.getSprite()));
            }
            g2.drawImage(drawDying.getSprite(), positionX, positionY, null);
        }
    }

    private void attack(Graphics2D g2) {
        //checking sprite is not null
        if(drawAttack.getSprite() != null) {
            //clearing the image so it does not show previous frames
            g2.clearRect(0, 0, getWidth(), getHeight());
            //determining direction of animation
            if(rightFacing) {
                drawAttack.setSprite(faceRight(drawAttack.getSprite()));
            }
            g2.drawImage(drawAttack.getSprite(), positionX, positionY, null);
        }
    }

    private void idle(Graphics2D g2) {
        //checking sprite is not null
        if(drawIdle.getSprite() != null) {
            //clearing the image so it does not show previous frames
            g2.clearRect(0, 0, getWidth(), getHeight());
            if(rightFacing) {
                drawIdle.setSprite(faceRight(drawIdle.getSprite()));
            }
            g2.drawImage(drawIdle.getSprite(), positionX, positionY, null);
        }
    }

    private BufferedImage faceRight(BufferedImage sprite) {
        //modified from stack overflow and Arnos' example
        //makes X-axis negative, inverting the image so it is facing the other dirrection
        final AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-255, 0);
        final AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(sprite, null);
    }

    public static void main(String[] args) {
        System.out.println( "-------------------------------------" );
        System.out.println( "159.235 Assignment 2, Semester 2 2016" );
        System.out.println( "Submitted by: Pitts, Kelly, 09098321" );
        System.out.println( "-------------------------------------" );

        SwingUtilities.invokeLater(Animation::new);
    }
}
