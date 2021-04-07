package scene;

import core.Vector2;
import scene.physics.Body;
import scene.physics.Shape;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all {@link Scene} objects.
 * The nodes are organised in a tree belonging to the scene.
 * Each node has a parent (except the {@link SceneRoot} and any number of children.
 */
public class Node {
    private final ArrayList<Node> children = new ArrayList<>();
    private Node owner = null;
    private Body body = null;
    private boolean destroyed = false;

    private final Vector2 position = new Vector2();
    private double orient = 0.0; // Orientation in radians

    private boolean updatingChildNodes = false;
    private final ArrayList<Node> childrenToRemove = new ArrayList<>();

    public Node() { }

    /**
     * @return Return true if the node is inside a {@link Scene}. A node is connected to
     *         the scene tree by his parent until the root node (see {@link SceneRoot}).
     */
    public boolean isInScene() {
        return scene() != null;
    }
    /**
     * @return Return true if the node has a physics body.
     */
    public boolean hasBody() {
        return body != null;
    }
    /**
     * @return The {@link Scene} object on which the node belong or null if the
     *         node is not connected to the scene tree (see {@link Node#isInScene()}).
     */
    public Scene scene() {
        return owner == null ? null : owner.scene();
    }
    /**
     * @return The parent of this node
     */
    public Node owner() {
        return owner;
    }
    /**
     * @return Children of this node
     */
    public List<Node> children() {
        return Collections.unmodifiableList(children);
    }
    /**
     * @return The physics body of this node
     */
    public Body body() {
        return body;
    }
    /**
     * @return Return true if the node was destroyed from the scene tree (see {@link Node#remove()}).
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * @return Relative position of the node
     */
    public Vector2 position() {
        return position;
    }
    /**
     * @return Relative orientation of the node
     */
    public double orient() {
        return orient;
    }
    /**
     * Compute the absolute position of the node in the world
     * @return Absolute position of the node
     */
    public Vector2 worldPosition() {
        return owner != null ? position().clone().rotate(owner.worldOrient()).addi(owner.worldPosition()) : position();
    }
    /**
     * Compute the absolute orientation of the node in the world
     * @return Absolute orientation of the node
     */
    public double worldOrient() {
        return owner != null ? owner.worldOrient() + orient() : orient();
    }

    /**
     * Set the relative position of the node
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void setPosition(double x, double y) {
        position.set(x, y);
    }
    /**
     * Set the relative position of the node
     * @param pos The coordinates
     */
    public void setPosition(Vector2 pos) {
        position.set(pos);
    }
    /**
     * Set the relative orientation of the node
     * @param orient The orientation in radian
     */
    public void setOrient(double orient) {
        this.orient = orient;
    }

    /**
     * Add a child node and call {@link Node#init()} of the child.
     * @param childNode The child node.
     * @return The added child node
     */
    public<T extends Node> T addChild(T childNode) {
        Node child = childNode;
        if(child.owner != null) {
            throw new RuntimeException("The child already has a parent");
        }
        children.add(child);
        child.owner = this;
        child.init();
        return childNode;
    }
    /**
     * Add a child node below the given node in the list of children.
     * @param node The node below which the child will be added
     * @param childNode The child node
     * @return The added child node
     */
    public<T extends Node> T addChildBelow(Node node, T childNode) {
        Node child = childNode;
        if(child.owner != null) {
            throw new RuntimeException("The child already has a parent");
        }
        int nodeIdx = children.indexOf(node);
        if(nodeIdx == -1) {
            children.add(child);
        } else {
            children.add(nodeIdx + 1, child);
        }
        child.owner = this;
        child.init();
        return childNode;
    }
    /**
     * Remove a child node from the list of children.
     * Also removes the body of the child node.
     * If this method is called during a child node update,
     * the remove will be processed after the update.
     * @param childNode The child node to remove.
     */
    public void removeChild(Node childNode) {
        if(children.contains(childNode)) {
            childNode.destroy();

            if(updatingChildNodes) {
                childrenToRemove.add(childNode);
            } else {
                children.remove(childNode);
            }
        }
    }

    /**
     * Remove all children nodes.
     * Also removes the body of the child node.
     * If this method is called during a child node update,
     * the remove will be processed after the update.
     */
    public void removeAllChildren() {
        for(var childNode : children) {
            childNode.destroy();
        }
        if(updatingChildNodes) {
            childrenToRemove.addAll(children);
        } else {
            children.clear();
        }
    }

    /**
     * Set a body to the node
     * @param shape The shape of the body
     * @param mode The mode of the body. See {@link Body.Mode}
     * @return The body
     */
    public Body setBody(Shape shape, Body.Mode mode) {
        this.body = scene().physics().add(this, shape, mode);
        return this.body;
    }

    /**
     * Remove the body of the node
     */
    public void removeBody() {
        if(this.body != null) {
            scene().physics().remove(this.body);
            this.body = null;
        }
    }

    /**
     * Removes this node from the scene.
     * Also removes the body of this node.
     * Call {@link Node#removeChild(Node)} ()} of the owner.
     */
    public void remove() {
        if(owner != null) {
            owner.removeChild(this);
        }
    }

    /**
     * Initialization method called after adding the node to the scene tree.
     */
    protected void init() {
        boolean lastUpdateFlag = updatingChildNodes;
        updatingChildNodes = true; // Prevent child removing during loop
        for(var child : children) {
            child.init();
        }
        updatingChildNodes = lastUpdateFlag;
    }

    /**
     * Destroy method called when {@link Node#remove()} or {@link Node#removeChild(Node)} is called with this node.
     * This method should be overrided to free resources and allow this object to be garbage collected.
     */
    protected void destroy() {
        boolean lastUpdateFlag = updatingChildNodes;
        updatingChildNodes = true; // Prevent child removing during loop
        for (var child : children) {
            child.destroy();
        }
        updatingChildNodes = lastUpdateFlag;
        if (this.body != null) {
            scene().physics().remove(this.body);
        }
    }

    /**
     * Update method called each update tick (See {@link core.MainLoop})
     */
    protected void update() {
        updateChildNodes();
    }

    /**
     * Render method called each render frame (See {@link core.MainLoop})
     * @param g The graphics context
     */
    protected void render(Graphics2D g) {
        AffineTransform at = new AffineTransform(g.getTransform());

        at.translate(position.x, position.y);
        at.rotate(orient);

        g.setTransform(at);

        boolean lastUpdateFlag = updatingChildNodes;
        updatingChildNodes = true; // Prevent child removing during loop
        for(var child : children) {
            var gCopy = (Graphics2D) g.create();
            child.render(gCopy);
        }
        updatingChildNodes = lastUpdateFlag;
    }

    /**
     * Update nodes and allow node remove during update
     */
    private void updateChildNodes() {
        updatingChildNodes = true;

        if(!childrenToRemove.isEmpty()) {
            children.removeAll(childrenToRemove);
            childrenToRemove.clear();
        }

        for (int childIdx = 0; childIdx < children.size(); childIdx++) {
            Node child = children.get(childIdx);

            child.update();

            if(!childrenToRemove.isEmpty()) {
                for (int i = 0; i < children.size(); i++) {
                    if(childrenToRemove.contains(children.get(i))) {
                        children.remove(i);
                        if(i <= childIdx) {
                            childIdx--;
                        }
                        i--;
                    }
                }
                childrenToRemove.clear();
            }
        }
        updatingChildNodes = false;
    }
}
