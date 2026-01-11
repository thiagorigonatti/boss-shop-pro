package org.black_ixx.bossshop.managers.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class ItemDataPartCustomSkull extends ItemDataPart {

    public static URL extractSkinUrl(String input) throws Exception {

        if (input.startsWith("http://") || input.startsWith("https://")) {
            return new URL(input);
        }

        try {
            String decoded = new String(
                    Base64.getDecoder().decode(input),
                    StandardCharsets.UTF_8
            );

            JsonObject root = JsonParser.parseString(decoded).getAsJsonObject();
            String url = root
                    .getAsJsonObject("textures")
                    .getAsJsonObject("SKIN")
                    .get("url")
                    .getAsString();

            return new URL(url);

        } catch (Exception ignore) {
        }

        return new URL("https://textures.minecraft.net/texture/" + input);
    }


    public static ItemStack transformSkull(ItemStack item, String input) {
        if (input == null || input.isEmpty()) return item;
        if (!(item.getItemMeta() instanceof SkullMeta meta)) return item;

        try {
            URL skinUrl = extractSkinUrl(input);

            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(skinUrl);

            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);

        } catch (Exception e) {
            Bukkit.getLogger().warning("[BossShop] Failed to apply skull texture: " + input);
            e.printStackTrace();
        }

        return item;
    }


    public static String readSkullTexture(ItemStack item) {
        if (!(item.getItemMeta() instanceof SkullMeta meta)) return null;

        PlayerProfile profile = meta.getOwnerProfile();
        if (profile == null) return null;

        PlayerTextures textures = profile.getTextures();
        if (textures == null || textures.getSkin() == null) return null;

        return textures.getSkin().toString();
    }

    @Override
    public ItemStack transform(ItemStack item, String used_name, String argument) {
        if (!(item.getItemMeta() instanceof SkullMeta)) {
            ClassManager.manager.getBugFinder().warn(
                    "Mistake in Config: Itemdata of type '" + used_name + "' with value '" + argument +
                            "' can not be added to an item with material '" + item.getType().name() +
                            "'. Transforming material into '" + Material.PLAYER_HEAD + "'."
            );
            item.setType(Material.PLAYER_HEAD);
        }

        return transformSkull(item, argument);
    }

    @Override
    public int getPriority() {
        return PRIORITY_EARLY;
    }

    @Override
    public boolean removeSpaces() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"customskull", "skull"};
    }

    @Override
    public List<String> read(ItemStack item, List<String> output) {
        String skullTexture = readSkullTexture(item);
        if (skullTexture != null) {
            output.add("customskull:" + skullTexture);
        }
        return output;
    }

    @Override
    public boolean isSimilar(ItemStack shop_item, ItemStack player_item, BSBuy buy, Player p) {
        return true;
    }
}
