package gyromite.scene;

import gyromite.core.Rect2;
import gyromite.core.Size2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * The frames of an animation of an {@link AnimatedSprite}
 */
public class SpriteFrames {
    private final String id;
    private final AnimatedSprite sprite;
    private final ArrayList<BufferedImage> frames = new ArrayList<>();
    private boolean loop = false;
    private double speed = 1.0;

    public SpriteFrames(String id, AnimatedSprite sprite) {
        this.id = id;
        this.sprite = sprite;
    }

    /**
     * @return The id of the animation
     */
    public String id() {
        return id;
    }
    /**
     * @return The number of frames of the animation
     */
    public int frameCount() {
        return frames.size();
    }
    /**
     * @param idx The position of the frame in the animation
     * @return The frame at the position <code>idx</code>
     */
    public BufferedImage getFrame(int idx) {
        return frames.get(idx);
    }
    /**
     * @return Return <code>true</code> if the animation loop
     */
    public boolean isLoop() {
        return loop;
    }
    /**
     * @return The speed of the animation in frame per second
     */
    public double speed() {
        return speed;
    }

    /**
     * Add a frame to the animation
     */
    public SpriteFrames addFrame(BufferedImage image) {
        frames.add(image);
        return this;
    }
    public SpriteFrames addFrame(BufferedImage image, int pos) {
        frames.add(pos, image);
        return this;
    }
    public SpriteFrames addFrame(BufferedImage image, Rect2 region) {
        return addFrame(image, region, frames.size());
    }
    public SpriteFrames addFrame(BufferedImage image, Rect2 region, int pos) {
        Size2 s = region.size();
        return addFrame(image.getSubimage((int)region.min.x, (int)region.min.y, (int)s.width, (int)s.height), pos);
    }
    public SpriteFrames addFrames(BufferedImage image, int hframes, int vframes, int frameBegin, int frameEnd) {
        return addFrames(image, hframes, vframes, frameBegin, frameEnd, frames.size());
    }
    public SpriteFrames addFrames(BufferedImage image, int hframes, int vframes, int frameBegin, int frameEnd, int pos) {
        for(int frame = frameBegin; frame <= frameEnd; frame++) {
            int frameX = frame % hframes;
            int frameY = frame / hframes;
            int x1 = frameX * image.getWidth() / hframes;
            int y1 = frameY * image.getHeight() / vframes;
            var subImg = image.getSubimage(x1, y1, image.getWidth() / hframes, image.getHeight() / vframes);
            frames.add(pos++, subImg);
        }
        return this;
    }
    public SpriteFrames addFrame(String image) {
        return addFrame(getImage(image));
    }
    public SpriteFrames addFrame(String image, int pos) {
        return addFrame(getImage(image), pos);
    }
    public SpriteFrames addFrame(String image, Rect2 region) {
        return addFrame(getImage(image), region);
    }
    public SpriteFrames addFrame(String image, Rect2 region, int pos) {
        return addFrame(getImage(image), region, pos);
    }
    public SpriteFrames addFrames(String image, int hframes, int vframes, int frameBegin, int frameEnd) {
        return addFrames(getImage(image), hframes, vframes, frameBegin, frameEnd);
    }
    public SpriteFrames addFrames(String image, int hframes, int vframes, int frameBegin, int frameEnd, int pos) {
        return addFrames(getImage(image), hframes, vframes, frameBegin, frameEnd, pos);
    }

    /**
     * Remove a frame from the animation
     */
    public SpriteFrames removeFrame(int idx) {
        frames.remove(idx);
        return this;
    }

    /**
     * Set whether if the animation loop or not
     * If the animation loop, it will go come back to the first frame
     * and will be playing again when it ends.
     * @param loop <code>true</code> if the animation loop
     */
    public SpriteFrames loop(boolean loop) {
        this.loop = loop;
        return this;
    }
    /**
     * Set the speed of the animation in frame per second
     * @param speed The speed of the animation
     */
    public SpriteFrames setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    private BufferedImage getImage(String id) {
        BufferedImage image = sprite.scene().resources().getImage(id);
        if(image == null) {
            throw new RuntimeException(String.format("Resource image '%s' does not exists", id));
        }
        return image;
    }
}
