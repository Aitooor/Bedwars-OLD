package dev.eugenio.nasgarbedwars.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class SkinUtil {
    public static void getSkin(final String name, final Player author, final MineSkinFetcher.Callback callback) {
        try {
            final URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            final InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            final String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            final URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            final InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            final JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            final String texture = textureProperty.get("value").getAsString();
            final String signature = textureProperty.get("signature").getAsString();

            callback.call(new Skin(texture, signature));
        } catch (IOException e) {
            System.err.println("Error al obtener signature de Skin desde los servidores de Mojang.");
            e.printStackTrace();
            callback.failed();
            author.sendMessage("Ha habido un pequeño problema poniendo al NPC de estadísticas tu skin. Hemos registrado este error y lo arreglaremos pronto. ¡Ups!");
        }
    }
}
