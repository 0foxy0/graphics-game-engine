package de.foxy.engine;

import de.foxy.engine.components.Component;
import de.foxy.engine.utils.Transform;

import java.util.ArrayList;

public class GameObject {
    private static int ID_COUNTER = 0;
    private String name;
    private final ArrayList<Component> components = new ArrayList<>();
    public Transform transform;
    private transient int uid;

    public GameObject(String name) {
        this.name = name;
        this.transform = new Transform();
        this.uid = ID_COUNTER++;
    }

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
        this.uid = ID_COUNTER++;
    }

    public void start() {
        for (Component component : components) {
            component.start();
        }
    }

    public void update(double deltaTime) {
        for (Component component : components) {
            component.update(deltaTime);
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component component : components) {
            if (!componentClass.isAssignableFrom(component.getClass())) {
                continue;
            }

            try {
                return componentClass.cast(component);
            } catch (ClassCastException err) {
                err.printStackTrace();
                throw new RuntimeException("Couldn't cast component");
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);

            if (!componentClass.isAssignableFrom(component.getClass())) {
                continue;
            }

            components.remove(i);
            break;
        }
    }

    public void addComponent(Component component) {
        components.add(component);
        component.gameObject = this;
    }

    public void destroy() {
        Window.getCurrentScene().removeGameObjectFromScene(this);
        components.clear();
    }

    public int getUid() {
        return this.uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name + ": " + transform.position + " | " + transform.scale + "\n" + components;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameObject go)) {
            return false;
        }
        return go.getUid() == uid;
    }
}
