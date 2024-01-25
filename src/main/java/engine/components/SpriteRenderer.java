package engine.components;

import engine.Component;
import engine.Transform;
import engine.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite = new Sprite(null);
    private Transform lastTransform;
    private boolean hasChanged = false;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    public SpriteRenderer() {
        this.color = new Vector4f();
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void start() {
        lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(double deltaTime) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            hasChanged = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoords() {
        return sprite.getTextureCoords();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        hasChanged = true;
    }

    public void setColor(Vector4f color) {
        if (this.color.equals(color)) {
            return;
        }

        this.color.set(color);
        hasChanged = true;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void changeAcknowledged() {
        hasChanged = false;
    }
}
