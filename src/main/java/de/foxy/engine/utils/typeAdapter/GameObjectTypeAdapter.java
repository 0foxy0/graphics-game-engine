package de.foxy.engine.utils.typeAdapter;

import com.google.gson.*;
import de.foxy.engine.GameObject;
import de.foxy.engine.components.Component;
import de.foxy.engine.components.SpriteRenderer;
import de.foxy.engine.renderer.Texture;
import de.foxy.engine.utils.AssetCollector;
import de.foxy.engine.utils.Transform;

import java.lang.reflect.Type;

public class GameObjectTypeAdapter implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String gOName = jsonObject.get("name").getAsString();
        JsonArray gOComponents = jsonObject.getAsJsonArray("components");
        Transform gOTransform = context.deserialize(jsonObject.get("transform"), Transform.class);

        GameObject gameObject = new GameObject(gOName, gOTransform);

        for (JsonElement element : gOComponents) {
            Component component = context.deserialize(element, Component.class);

            if (component instanceof SpriteRenderer spriteRenderer && spriteRenderer.getTexture() != null) {
                Texture texture = spriteRenderer.getTexture();
                spriteRenderer.setTexture(AssetCollector.getTexture(texture.getFilePath(), texture.doesPixelate()));
            }

            gameObject.addComponent(component);
        }

        return gameObject;
    }
}
