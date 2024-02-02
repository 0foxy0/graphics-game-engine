package de.foxy.engine.utils.geometry;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Box2D extends DebugElement {
    private Vector2f center, dimensions;
    private float rotation = 0;

    public Box2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifetime) {
        super(color, lifetime);
        this.center = center;
        this.dimensions = dimensions;
        this.rotation = rotation;
    }
    public Box2D(Vector2f center, Vector2f dimensions, float rotation) {
        this.center = center;
        this.dimensions = dimensions;
        this.rotation = rotation;
    }
    public Box2D(Vector2f center, Vector2f dimensions, int lifetime) {
        super(lifetime);
        this.center = center;
        this.dimensions = dimensions;
    }
    public Box2D(Vector2f center, Vector2f dimensions) {
        this.center = center;
        this.dimensions = dimensions;
    }

    public Vector2f getCenter() {
        return center;
    }

    public Vector2f getDimensions() {
        return dimensions;
    }

    public float getRotation() {
        return rotation;
    }
}
