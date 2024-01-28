package de.foxy.engine.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {
    private Vector2f from, to;
    private Vector3f color = new Vector3f(0, 0, 0);
    private int lifetime = 60;

    public Line2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifetime = lifetime;
    }

    public Line2D(Vector2f from, Vector2f to, int lifetime) {
        this.from = from;
        this.to = to;
        this.lifetime = lifetime;
    }

    public Line2D(Vector2f from, Vector2f to, Vector3f color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }

    public Line2D(Vector2f from, Vector2f to) {
        this.from = from;
        this.to = to;
    }

    public int beginFrame() {
        lifetime--;
        return lifetime;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color;
    }
}
