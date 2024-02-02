package de.foxy.engine.utils.geometry;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Circle extends DebugElement {
    private Vector2f center;
    private float radius;

    public Circle(Vector2f center, float radius) {
        this.center = center;
        this.radius = radius;
    }
    public Circle(Vector2f center, float radius, Vector3f color, int lifetime) {
        super(color, lifetime);
        this.center = center;
        this.radius = radius;
    }
    public Circle(Vector2f center, float radius, int lifetime) {
        super(lifetime);
        this.center = center;
        this.radius = radius;
    }
    public Circle(Vector2f center, float radius, Vector3f color) {
        super(color);
        this.center = center;
        this.radius = radius;
    }

    public Vector2f getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }
}
