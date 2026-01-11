package org.black_ixx.bossshop.misc;

import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Misc {

    /**
     * Fix the lore from a stringlist
     * @param itemData the sting list
     * @return fixed lore list
     */
    public static List<String> fixLore(List<String> itemData) {
        Map<Integer, String> lore = null;
        List<String> new_list = null;
        int highest = -1;

        for (String line : itemData) {
            if (line.toLowerCase().startsWith("lore")) {
                String[] parts = line.split(":", 2);
                String start = parts[0];
                if (start.length() > "lore".length()) {

                    try {
                        int i = Integer.parseInt(start.replace("lore", "")) - 1;

                        if (lore == null) {
                            lore = new HashMap<Integer, String>();
                            new_list = new ArrayList<>();
                        }

                        lore.put(i, parts[1]);
                        highest = Math.max(highest, i);

                    } catch (NumberFormatException e) {
                        //Fail
                    }

                }
            }
        }

        if (new_list != null) {
            for (String line : itemData) {
                if (!line.toLowerCase().startsWith("lore")) {
                    new_list.add(line);
                }
            }
            for (int i = 0; i <= highest; i++) {
                String s = "lore:";
                if (lore.containsKey(i)) {
                    s += lore.get(i);
                }
                new_list.add(s);
            }
        }


        if (new_list != null) {
            return new_list;
        }
        return itemData;
    }

    private static Sound resolveSound(String input) {

        try {
            return Sound.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
        }

        try {
            NamespacedKey key = NamespacedKey.fromString(input.toLowerCase(Locale.ROOT));
            if (key != null) {
                for (Sound s : Sound.values()) {
                    if (s.getKey().equals(key)) {
                        return s;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Play a sound for a player
     * @param p the player to play the sound for
     * @param sound the sound to play
     */
    public static void playSound(Player p, String sound) {
        if (sound == null || sound.isEmpty()) return;

        String[] parts = sound.split(":");
        String soundName = parts[0];

        float volume = parts.length >= 2 ? (float) InputReader.getDouble(parts[1], 1) : 1f;
        float pitch  = parts.length >= 3 ? (float) InputReader.getDouble(parts[2], 1) : 1f;

        Sound bukkitSound = resolveSound(soundName);

        if (bukkitSound != null) {
            p.playSound(p.getLocation(), bukkitSound, volume, pitch);
        } else {
            Bukkit.getLogger().warning("[BossShop] Unknown sound: " + soundName);
        }
    }


    /**
     * Get the item in the player's main hand
     * @param p player to get item from
     * @return item
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getItemInMainHand(Player p) {
        ItemStack item = null;
        try {
            item = p.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError e) {
            item = p.getItemInHand();
        }
        return item;
    }

}
