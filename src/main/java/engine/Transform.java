package engine;

import org.joml.Vector2f;

public class Transform {
    public Vector2f position;
    public Vector2f scale;

    public Transform() {
        position = new Vector2f();
        scale = new Vector2f();
    }

    public Transform(Vector2f position) {
        this.position = position;
        this.scale = new Vector2f();
    }

    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    public Transform copy() {
        return new Transform(new Vector2f(position), new Vector2f(scale));
    }

    public void copy(Transform to) {
        to.position.set(position);
        to.scale.set(scale);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transform t)) {
            return false;
        }

        return t.position.equals(position) && t.scale.equals(scale);
    }
}
