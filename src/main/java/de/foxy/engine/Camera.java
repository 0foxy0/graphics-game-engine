package de.foxy.engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix = new Matrix4f(), viewMatrix = new Matrix4f(), inverseProjection = new Matrix4f(), inverseView = new Matrix4f();
    private Vector3f position;

    public Camera(Vector2f position) {
        this.position = new Vector3f(position, 20f);
        adjustProjection();
    }

    public Camera(Vector3f position) {
        this.position = position;
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0f, 1920, 0f, 1080, 0f, 100f);
        projectionMatrix.invert(inverseProjection);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0f, 0f, -1f).add(position.x, position.y, 0f);
        Vector3f cameraUp = new Vector3f(0f, 1f, 0f);

        viewMatrix.identity();
        viewMatrix.lookAt(position, cameraFront, cameraUp);
        viewMatrix.invert(inverseView);

        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    public Matrix4f getInverseView() {
        return inverseView;
    }
}
