package de.foxy.engine.utils.geometry;

import org.joml.Vector3f;

public class DebugElement {
    private Vector3f color = new Vector3f(0, 0, 0);
    // Unit: Frames
    private int lifetime = 60;

    public DebugElement() {}
    public DebugElement(Vector3f color, int lifetime) {
        this.color = color;
        this.lifetime = lifetime;
    }
    public DebugElement(int lifetime) {
        this.lifetime = lifetime;
    }
    public DebugElement(Vector3f color) {
        this.color = color;
    }

    public int beginFrame() {
        if (lifetime == Integer.MAX_VALUE) {
            return lifetime;
        }
        lifetime--;
        return lifetime;
    }

    public Vector3f getColor() {
        return color;
    }

    public int getLifetime() {
        return lifetime;
    }
}
