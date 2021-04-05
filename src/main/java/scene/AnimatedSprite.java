package scene;

import core.MainLoop;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * A sprite node that can use multiple image for animations.
 * Animations are created using {@link SpriteFrames}.
 * Each animation is associated to an id.
 */
public class AnimatedSprite extends SpriteBase {
    private final HashMap<String, SpriteFrames> animations = new HashMap<>();
    boolean playing = false;
    boolean playBackwards = false;
    String currentAnimation = "";
    int currentAnimationFrame = 0;
    private long frameTime = 0;
    private double speedScale = 1.0;

    public AnimatedSprite() {
        super();
    }

    /**
     * @param id The id of the animation.
     * @return <code>true</code> If the animation with the given id exists.
     */
    public boolean hasAnimation(String id) {
        return animations.containsKey(id);
    }
    /**
     * @param id The id of the animation.
     * @return The animation with the given id or null if the animation does not exist.
     */
    public SpriteFrames animation(String id) {
        return animations.get(id);
    }
    /**
     * @return Return if an animation is playing.
     */
    public boolean isPlaying() {
        return playing;
    }
    /**
     * @return Return if the current animation is playing backwards.
     */
    public boolean isPlayingBackwards() {
        return playing && playBackwards;
    }
    /**
     * @return The index of the current animation.
     */
    public String currentAnimation() {
        return currentAnimation;
    }
    /**
     * @return The position of the current frame of the current animation.
     */
    public int currentFrame() {
        return currentAnimationFrame;
    }
    /**
     * @return The speed scale of animations. The animations speed will be multiplied by this value.
     */
    public double speedScale() {
        return speedScale;
    }

    /**
     * Set the speed scale of animations.
     * The animations speed will be multiplied by this value.
     */
    public void setSpeedScale(double speedScale) {
        this.speedScale = speedScale;
    }

    /**
     * Add an animation with the given id.
     * @param id The id of the animation.
     * @return The animation frames resources. See {@link SpriteFrames}.
     */
    public SpriteFrames addAnimation(String id) {
        SpriteFrames a = new SpriteFrames(id, this);
        animations.put(id, a);
        return a;
    }

    /**
     * Play the given animation.
     * @param id The id of the animation.
     */
    public void play(String id) {
        play(id, false);
    }
    /**
     * Play the given animation.
     * @param id The id of the animation.
     * @param backwards If <code>true</code> the animation will be played backward.
     */
    public void play(String id, boolean backwards) {
        if(hasAnimation(id)) {
            if(isPlaying()) {
                stop();
            }
            if(!id.equals(currentAnimation)) {
                currentAnimation = id;
                if(backwards) {
                    currentAnimationFrame = animation(id).frameCount() - 1;
                } else {
                    currentAnimationFrame = 0;
                }
            }
            frameTime = 0;
            playBackwards = backwards;
            playing = true;
        }
    }

    /**
     * Stop the current animation without resetting the frame counter.
     * The animation can be resumed by calling {@link AnimatedSprite#play(String)}.
     */
    public void stop() {
        playing = false;
    }
    /**
     * Stop the current animation and reset the frame counter.
     */
    public void reset() {
        currentAnimation = "";
        currentAnimationFrame = 0;
        frameTime = 0;
        playing = false;
    }

    @Override
    protected void update() {
        super.update();

        if(!isPlaying()) {
            return;
        }
        SpriteFrames anim = animation(currentAnimation);
        if(anim == null) {
            return;
        }

        frameTime += MainLoop.NANOS_PER_TICK;

        if(frameTime > MainLoop.NANOSECOND / (anim.speed() * speedScale)) {
            if(playBackwards) {
                currentAnimationFrame--;
            } else {
                currentAnimationFrame++;
            }
            boolean looped = false;
            if(currentAnimationFrame >= anim.frameCount()) {
                currentAnimationFrame = 0;
                looped = true;
            } else if(currentAnimationFrame < 0) {
                currentAnimationFrame = anim.frameCount() - 1;
                looped = true;
            }
            if(looped && !anim.isLoop()) {
                stop();
            }
            frameTime = 0;
        }
    }

    @Override
    protected void render(Graphics2D g) {
        super.render(g);

        if(currentAnimation == null) {
            return;
        }
        SpriteFrames anim = animation(currentAnimation);
        if(anim == null || currentAnimationFrame < 0 || currentAnimationFrame >= anim.frameCount()) {
            return;
        }
        BufferedImage image = anim.getFrame(currentAnimationFrame);

        g.translate(-size().width / 2.0, -size().height / 2.0);
        g.scale(size().width / (double)image.getWidth(), size().height / (double)image.getHeight());

        g.drawImage(image, 0, 0, null);
    }
}
