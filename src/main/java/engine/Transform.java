package engine;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position;
    public Vector3f scale;

    public Transform() {
        position = new Vector3f();
        scale = new Vector3f();
    }

    public Transform(Vector2f position) {
        this.position = new Vector3f(position, 0);
        this.scale = new Vector3f();
    }

    public Transform(Vector3f position) {
        this.position = position;
        this.scale = new Vector3f();
    }

    public Transform(Vector3f position, Vector3f scale) {
        this.position = position;
        this.scale = scale;
    }

    public Transform(Vector2f position, Vector2f scale) {
        this.position = new Vector3f(position, 0);
        this.scale = new Vector3f(scale, 0);;
    }

    public Transform(Vector3f position, Vector2f scale) {
        this.position = position;
        this.scale = new Vector3f(scale, 0);
    }

    public Transform(Vector2f position, Vector3f scale) {
        this.position = new Vector3f(position, 0);;
        this.scale = scale;
    }

    public Transform copy() {
        return new Transform(new Vector3f(position), new Vector3f(scale));
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
