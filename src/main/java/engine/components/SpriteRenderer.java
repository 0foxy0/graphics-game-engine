package engine.components;

import engine.Component;
import engine.renderer.Texture;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Texture texture = null;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    public SpriteRenderer(Texture texture) {
        this.texture = texture;
        this.color = new Vector4f();
    }

    public SpriteRenderer() {
        this.color = new Vector4f();
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double deltaTime) {

    }

    public Vector4f getColor() {
        return color;
    }
}
