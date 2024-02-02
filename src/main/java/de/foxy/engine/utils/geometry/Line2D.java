package de.foxy.engine.utils.geometry;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D extends DebugElement {
    private Vector2f from, to;

    public Line2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        super(color, lifetime);
        this.from = from;
        this.to = to;
    }
    public Line2D(Vector2f from, Vector2f to, int lifetime) {
        super(lifetime);
        this.from = from;
        this.to = to;
    }
    public Line2D(Vector2f from, Vector2f to, Vector3f color) {
        super(color);
        this.from = from;
        this.to = to;
    }
    public Line2D(Vector2f from, Vector2f to) {
        this.from = from;
        this.to = to;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }
}
