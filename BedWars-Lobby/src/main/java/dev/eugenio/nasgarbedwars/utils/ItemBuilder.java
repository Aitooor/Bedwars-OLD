package dev.eugenio.nasgarbedwars.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder {
    public static ItemStack parse(ItemStack i, String[]... t) {
        String display = (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) ? i.getItemMeta().getDisplayName() : "";
        ItemMeta im = i.getItemMeta();
        for (String[] s : t) {
            String s1 = s[0];
            String s2 = s[1];
            String s3 = s[2];
            if (display.equals(s1)) {
                im.setDisplayName(display.replace(s1, s2));
                im.setLore(s3.isEmpty() ? new ArrayList<>() : Arrays.asList(s3.split("\\n")));
                break;
            }
        }
        if (im != null) addItemFlags(im);
        i.setItemMeta(im);
        return i;
    }

    public static ItemStack item(Material material, String displayName, String s) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        addItemFlags(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(ItemStack item, String displayName, List<String> s) {
        ItemStack itemStack = item.clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.hasLore()) itemMeta.getLore().clear();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s);
        addItemFlags(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(Material material, int n, String displayName, String s) {
        ItemStack itemStack = new ItemStack(material, n, (short)0);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        addItemFlags(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack item(Material material, int n, int data, String displayName, String s) {
        ItemStack itemStack = new ItemStack(material, n, (short)data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        addItemFlags(itemMeta);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack skull(Material material, int n, String displayName, String s, String owner) {
        ItemStack itemStack = new ItemStack(material, n);
        SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
        skullMeta.setOwner(owner);
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        addItemFlags(skullMeta);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public static ItemStack createSkull(String displayName, String lore, String url) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        gameProfile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        headMeta.setDisplayName(displayName);
        headMeta.setLore(lore.isEmpty() ? new ArrayList<>() : Arrays.asList(lore.split("\\n")));

        head.setItemMeta(headMeta);
        return head;
    }

    public static ItemStack nameLore(ItemStack itemStack, String displayName, String s) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(null);
        itemMeta.setLore(s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void addItemFlags(ItemMeta itemMeta) {
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
    }
}
