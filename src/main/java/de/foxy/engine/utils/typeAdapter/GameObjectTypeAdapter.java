package de.foxy.engine.utils.typeAdapter;

import com.google.gson.*;
import de.foxy.engine.Component;
import de.foxy.engine.GameObject;
import de.foxy.engine.Transform;

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
            gameObject.addComponent(component);
        }

        return gameObject;
    }
}
