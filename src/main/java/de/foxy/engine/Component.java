package de.foxy.engine;

public abstract class Component {
    public transient GameObject gameObject = null;

    public void start() {}
    public void update(double deltaTime) {}
}
