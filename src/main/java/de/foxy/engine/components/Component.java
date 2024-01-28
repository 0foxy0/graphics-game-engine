package de.foxy.engine.components;

import de.foxy.engine.GameObject;

public abstract class Component {
    public transient GameObject gameObject = null;

    public void start() {}
    public void update(double deltaTime) {}
}
