package scene;

import core.Mat2;
import core.Vector2;
import scene.physics.Body;
import scene.physics.Shape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Node {
    private final ArrayList<Node> children = new ArrayList<>();
    private Node owner = null;
    private Body body = null;

    private boolean updatingChildNodes = false;
    private final ArrayList<Node> childrenToRemove = new ArrayList<>();

    private Vector2 position = new Vector2();
    private double orient = 0.0; // Orientation in radians

    public Node() { }

    public boolean isInScene() {
        return scene() != null;
    }
    public boolean hasBody() {
        return body != null;
    }

    public Scene scene() {
        return owner == null ? null : owner.scene();
    }
    public Body body() {
        return body;
    }
    public Vector2 position() {
        return position;
    }
    public double orient() {
        return orient;
    }
    public Vector2 worldPosition() {
        return owner != null ? position().clone().rotate(owner.worldOrient()).addi(owner.worldPosition()) : position();
    }
    public double worldOrient() {
        return owner != null ? owner.worldOrient() + orient() : orient();
    }

    public void setPosition(double x, double y) {
        position.set(x, y);
    }
    public void setPosition(Vector2 pos) {
        position.set(pos);
    }
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
        int nodeIdx = children.indexOf(node);
        if(nodeIdx == -1) {
            children.add(child);
        } else {
            children.add(nodeIdx + 1, child);
        }
        child.owner = this;
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
            childNode.removeBody();
            if(updatingChildNodes) {
                childrenToRemove.add(childNode);
            } else {
                children.remove(childNode);
            }
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
    protected void init() { }

    protected void update() {
        updateChildNodes();
    }

    protected void render(Graphics2D g) {
        AffineTransform at = new AffineTransform(g.getTransform());

        at.translate(position.x, position.y);
        at.rotate(orient);

        g.setTransform(at);

        for(var child : children) {
            var gCopy = (Graphics2D) g.create();
            child.render(gCopy);
        }
    }

    /**
     * Update nodes and allow node remove during update
     */
    private void updateChildNodes() {
        updatingChildNodes = true;

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
