package dev.eugenio.nasgarbedwars.support.version.v1_8_R3;

import dev.eugenio.nasgarbedwars.api.events.player.PlayerKillEvent;
import dev.eugenio.nasgarbedwars.support.version.common.VersionCommon;
import dev.eugenio.nasgarbedwars.api.arena.IArena;
import dev.eugenio.nasgarbedwars.api.arena.shop.ShopHologram;
import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.entity.Despawnable;
import dev.eugenio.nasgarbedwars.api.exceptions.InvalidEffectException;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.server.NMSUtil;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFireball;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class v1_8_R3 extends NMSUtil {
    public v1_8_R3(Plugin paramPlugin, String paramString) {
        super(paramPlugin, paramString);
        try {
            setEggBridgeEffect("MOBSPAWNER_FLAMES");
        } catch (InvalidEffectException invalidEffectException) {
            invalidEffectException.printStackTrace();
        }
    }

    public void spawnSilverfish(Location paramLocation, ITeam paramITeam, double paramDouble1, double paramDouble2, int paramInt, double paramDouble3) {
        new Despawnable(Silverfish.spawn(paramLocation, paramITeam, paramDouble1, paramDouble2, paramInt, paramDouble3), paramITeam, paramInt, Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME, PlayerKillEvent.PlayerKillCause.SILVERFISH_FINAL_KILL, PlayerKillEvent.PlayerKillCause.SILVERFISH);
    }

    public void spawnIronGolem(Location paramLocation, ITeam paramITeam, double paramDouble1, double paramDouble2, int paramInt) {
        new Despawnable(IGolem.spawn(paramLocation, paramITeam, paramDouble1, paramDouble2, paramInt), paramITeam, paramInt, Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME, PlayerKillEvent.PlayerKillCause.IRON_GOLEM_FINAL_KILL, PlayerKillEvent.PlayerKillCause.IRON_GOLEM);
    }

    public void registerCommand(String paramString, Command paramCommand) {
        ((CraftServer)getPlugin().getServer()).getCommandMap().register(paramString, paramCommand);
    }

    public void sendTitle(Player paramPlayer, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3) {
        if (paramString1 != null && !paramString1.isEmpty()) {
            IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + paramString1 + "\"}");
            PacketPlayOutTitle packetPlayOutTitle1 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iChatBaseComponent);
            PacketPlayOutTitle packetPlayOutTitle2 = new PacketPlayOutTitle(paramInt1, paramInt2, paramInt3);
            (((CraftPlayer)paramPlayer).getHandle()).playerConnection.sendPacket(packetPlayOutTitle1);
            (((CraftPlayer)paramPlayer).getHandle()).playerConnection.sendPacket(packetPlayOutTitle2);
        }
        if (paramString2 != null && !paramString2.isEmpty()) {
            IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + paramString2 + "\"}");
            PacketPlayOutTitle packetPlayOutTitle1 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChatBaseComponent);
            PacketPlayOutTitle packetPlayOutTitle2 = new PacketPlayOutTitle(paramInt1, paramInt2, paramInt3);
            (((CraftPlayer)paramPlayer).getHandle()).playerConnection.sendPacket(packetPlayOutTitle1);
            (((CraftPlayer)paramPlayer).getHandle()).playerConnection.sendPacket(packetPlayOutTitle2);
        }
    }

    public void playAction(Player paramPlayer, String paramString) {
        CraftPlayer craftPlayer = (CraftPlayer)paramPlayer;
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + paramString + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, (byte)2);
        (craftPlayer.getHandle()).playerConnection.sendPacket(packetPlayOutChat);
    }

    public org.bukkit.inventory.ItemStack getItemInHand(Player paramPlayer) {
        return paramPlayer.getItemInHand();
    }

    public void hideEntity(org.bukkit.entity.Entity entity, Player player) {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entity.getEntityId());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
    }

    public boolean isArmor(org.bukkit.inventory.ItemStack paramItemStack) {
        if (CraftItemStack.asNMSCopy(paramItemStack) == null) return false;
        if (CraftItemStack.asNMSCopy(paramItemStack).getItem() == null) return false;
        return CraftItemStack.asNMSCopy(paramItemStack).getItem() instanceof net.minecraft.server.v1_8_R3.ItemArmor;
    }

    public boolean isTool(org.bukkit.inventory.ItemStack paramItemStack) {
        if (CraftItemStack.asNMSCopy(paramItemStack) == null)
            return false;
        if (CraftItemStack.asNMSCopy(paramItemStack).getItem() == null)
            return false;
        return CraftItemStack.asNMSCopy(paramItemStack).getItem() instanceof net.minecraft.server.v1_8_R3.ItemTool;
    }

    public boolean isSword(org.bukkit.inventory.ItemStack paramItemStack) {
        if (CraftItemStack.asNMSCopy(paramItemStack).getItem() == null)
            return false;
        return CraftItemStack.asNMSCopy(paramItemStack).getItem() instanceof net.minecraft.server.v1_8_R3.ItemSword;
    }

    public boolean isAxe(org.bukkit.inventory.ItemStack paramItemStack) {
        if (CraftItemStack.asNMSCopy(paramItemStack).getItem() == null)
            return false;
        return CraftItemStack.asNMSCopy(paramItemStack).getItem() instanceof net.minecraft.server.v1_8_R3.ItemAxe;
    }

    public boolean isBow(org.bukkit.inventory.ItemStack paramItemStack) {
        if (CraftItemStack.asNMSCopy(paramItemStack) == null)
            return false;
        if (CraftItemStack.asNMSCopy(paramItemStack).getItem() == null)
            return false;
        return CraftItemStack.asNMSCopy(paramItemStack).getItem() instanceof net.minecraft.server.v1_8_R3.ItemBow;
    }

    public boolean isProjectile(org.bukkit.inventory.ItemStack paramItemStack) {
        if (CraftItemStack.asNMSCopy(paramItemStack) == null)
            return false;
        if (CraftItemStack.asNMSCopy(paramItemStack).getItem() == null)
            return false;
        return CraftItemStack.asNMSCopy(paramItemStack).getItem() instanceof net.minecraft.server.v1_8_R3.IProjectile;
    }

    public void registerEntities() {
        registerEntity("Silverfish2", 60, Silverfish.class);
        registerEntity("IGolem", 99, IGolem.class);
        registerEntity("BwVilager", 120, VillagerShop.class);
    }

    public void setCollide(Player paramPlayer, IArena paramIArena, boolean paramBoolean) {
        paramPlayer.spigot().setCollidesWithEntities(paramBoolean);
    }

    public void minusAmount(Player paramPlayer, org.bukkit.inventory.ItemStack paramItemStack, int paramInt) {
        if (paramItemStack.getAmount() - paramInt <= 0) {
            paramPlayer.getInventory().removeItem(paramItemStack);
            return;
        }
        paramItemStack.setAmount(paramItemStack.getAmount() - paramInt);
        paramPlayer.updateInventory();
    }

    public void spawnShop(Location paramLocation, String paramString, List<Player> paramList, IArena paramIArena) {
        Location location = paramLocation.clone();
        spawnVillager(location);
        for (Player player : paramList) {
            String[] arrayOfString = Language.getMsg(player, paramString).split(",");
            if (arrayOfString.length == 1) {
                ArmorStand armorStand = createArmorStand(arrayOfString[0], location.clone().add(0.0D, 1.85D, 0.0D));
                new ShopHologram(Language.getPlayerLanguage(player).getIso(), armorStand, null, location, paramIArena);
                continue;
            }
            ArmorStand armorStand1 = createArmorStand(arrayOfString[0], location.clone().add(0.0D, 2.1D, 0.0D));
            ArmorStand armorStand2 = createArmorStand(arrayOfString[1], location.clone().add(0.0D, 1.85D, 0.0D));
            new ShopHologram(Language.getPlayerLanguage(player).getIso(), armorStand1, armorStand2, location, paramIArena);
        }
        for (ShopHologram shopHologram : ShopHologram.getShopHologram()) if (shopHologram.getA() == paramIArena) shopHologram.update();
    }

    public static class VillagerShop extends EntityVillager {
        VillagerShop(Location param1Location) {
            super(((CraftWorld)param1Location.getWorld()).getHandle());
            try {
                Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
                bField.setAccessible(true);
                Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
                cField.setAccessible(true);

                bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
                bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
                cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
                cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.goalSelector.a(0, new PathfinderGoalFloat(this));
            this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
            this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0.6D));
            this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
            this.goalSelector.a(9, new PathfinderGoalRandomStroll(this, 0.6D));
            this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
            setLocation(param1Location.getX(), param1Location.getY(), param1Location.getZ(), param1Location.getYaw(), param1Location.getPitch());
            ((CraftWorld)param1Location.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
            this.persistent = true;
        }

        public void move(double d0, double d1, double d2) {}

        public void collide(Entity param1Entity) {}

        public boolean damageEntity(DamageSource param1DamageSource, float param1Float) {
            return false;
        }

        public void g(double param1Double1, double param1Double2, double param1Double3) {}

        public void makeSound(String param1String, float param1Float1, float param1Float2) {}

        protected void initAttributes() {
            super.initAttributes();
            getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0D);
        }
    }

    private void spawnVillager(Location paramLocation) {
        VillagerShop villagerShop = new VillagerShop(paramLocation);
        ((CraftLivingEntity)villagerShop.getBukkitEntity()).setRemoveWhenFarAway(false);
    }

    public double getDamage(org.bukkit.inventory.ItemStack paramItemStack) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(paramItemStack);
        NBTTagCompound nBTTagCompound = itemStack.hasTag() ? itemStack.getTag() : new NBTTagCompound();
        return nBTTagCompound.getDouble("generic.attackDamage");
    }

    private static ArmorStand createArmorStand(String paramString, Location paramLocation) {
        ArmorStand armorStand = paramLocation.getWorld().spawn(paramLocation, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(paramString);
        return armorStand;
    }

    private void registerEntity(String paramString, int paramInt, Class<? extends EntityCreature> paramClass) {
        try {
            ArrayList<Map> arrayList = new ArrayList<>();
            for (Field field : EntityTypes.class.getDeclaredFields()) {
                if (field.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    field.setAccessible(true);
                    arrayList.add((Map) field.get(null));
                }
            }
            if (arrayList.get(2).containsKey(paramInt)) {
                arrayList.get(0).remove(paramString);
                arrayList.get(2).remove(paramInt);
            }
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, paramClass, paramString, paramInt);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setSource(TNTPrimed paramTNTPrimed, Player paramPlayer) {
        EntityLiving entityLiving = ((CraftLivingEntity)paramPlayer).getHandle();
        EntityTNTPrimed entityTNTPrimed = ((CraftTNTPrimed)paramTNTPrimed).getHandle();
        try {
            Field field = EntityTNTPrimed.class.getDeclaredField("source");
            field.setAccessible(true);
            field.set(entityTNTPrimed, entityLiving);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void voidKill(Player paramPlayer) {
        ((CraftPlayer)paramPlayer).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, 1000.0F);
    }

    public void hideArmor(Player paramPlayer1, Player paramPlayer2) {
        if (paramPlayer1.equals(paramPlayer2)) return;
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment1 = new PacketPlayOutEntityEquipment(paramPlayer1.getEntityId(), 0, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR)));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment2 = new PacketPlayOutEntityEquipment(paramPlayer1.getEntityId(), 1, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR)));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment3 = new PacketPlayOutEntityEquipment(paramPlayer1.getEntityId(), 2, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR)));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment4 = new PacketPlayOutEntityEquipment(paramPlayer1.getEntityId(), 3, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR)));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment5 = new PacketPlayOutEntityEquipment(paramPlayer1.getEntityId(), 4, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR)));
        PlayerConnection playerConnection = (((CraftPlayer)paramPlayer2).getHandle()).playerConnection;
        playerConnection.sendPacket(packetPlayOutEntityEquipment1);
        playerConnection.sendPacket(packetPlayOutEntityEquipment2);
        playerConnection.sendPacket(packetPlayOutEntityEquipment3);
        playerConnection.sendPacket(packetPlayOutEntityEquipment4);
        playerConnection.sendPacket(packetPlayOutEntityEquipment5);
    }

    public void showArmor(Player paramPlayer1, Player paramPlayer2) {
        if (paramPlayer1.equals(paramPlayer2)) return;
        EntityPlayer entityPlayer1 = ((CraftPlayer)paramPlayer1).getHandle();
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment1 = new PacketPlayOutEntityEquipment(entityPlayer1.getId(), 0, entityPlayer1.inventory.getItemInHand());
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment2 = new PacketPlayOutEntityEquipment(entityPlayer1.getId(), 4, entityPlayer1.inventory.getArmorContents()[3]);
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment3 = new PacketPlayOutEntityEquipment(entityPlayer1.getId(), 3, entityPlayer1.inventory.getArmorContents()[2]);
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment4 = new PacketPlayOutEntityEquipment(entityPlayer1.getId(), 2, entityPlayer1.inventory.getArmorContents()[1]);
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment5 = new PacketPlayOutEntityEquipment(entityPlayer1.getId(), 1, entityPlayer1.inventory.getArmorContents()[0]);
        EntityPlayer entityPlayer2 = ((CraftPlayer)paramPlayer2).getHandle();
        if (paramPlayer1 != paramPlayer2) entityPlayer2.playerConnection.sendPacket(packetPlayOutEntityEquipment1);
        entityPlayer2.playerConnection.sendPacket(packetPlayOutEntityEquipment2);
        entityPlayer2.playerConnection.sendPacket(packetPlayOutEntityEquipment3);
        entityPlayer2.playerConnection.sendPacket(packetPlayOutEntityEquipment4);
        entityPlayer2.playerConnection.sendPacket(packetPlayOutEntityEquipment5);
    }

    public void spawnDragon(Location paramLocation, ITeam paramITeam) {
        paramLocation.getWorld().spawnEntity(paramLocation, EntityType.ENDER_DRAGON);
    }

    public void colorBed(ITeam paramITeam) {}

    public void registerTntWhitelist() {
        try {
            Field field = Block.class.getDeclaredField("durability");
            field.setAccessible(true);
            field.set(Block.getByName("glass"), 300.0F);
            field.set(Block.getByName("stained_glass"), 300.0F);
            field.set(Block.getByName("end_stone"), 69.0F);
        } catch (NoSuchFieldException|IllegalAccessException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }
    }

    public void setBlockTeamColor(org.bukkit.block.Block paramBlock, TeamColor paramTeamColor) {
        paramBlock.setData(paramTeamColor.itemByte());
    }

    public org.bukkit.inventory.ItemStack addCustomData(org.bukkit.inventory.ItemStack paramItemStack, String paramString) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(paramItemStack);
        NBTTagCompound nBTTagCompound = itemStack.getTag();
        if (nBTTagCompound == null) {
            nBTTagCompound = new NBTTagCompound();
            itemStack.setTag(nBTTagCompound);
        }
        nBTTagCompound.setString("BedWars", paramString);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    public org.bukkit.inventory.ItemStack setTag(org.bukkit.inventory.ItemStack paramItemStack, String paramString1, String paramString2) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(paramItemStack);
        NBTTagCompound nBTTagCompound = itemStack.getTag();
        if (nBTTagCompound == null) {
            nBTTagCompound = new NBTTagCompound();
            itemStack.setTag(nBTTagCompound);
        }
        nBTTagCompound.setString(paramString1, paramString2);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    public boolean isCustomBedWarsItem(org.bukkit.inventory.ItemStack paramItemStack) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(paramItemStack);
        if (itemStack == null)
            return false;
        NBTTagCompound nBTTagCompound = itemStack.getTag();
        if (nBTTagCompound == null)
            return false;
        return nBTTagCompound.hasKey("BedWars");
    }

    public String getCustomData(org.bukkit.inventory.ItemStack paramItemStack) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(paramItemStack);
        NBTTagCompound nBTTagCompound = itemStack.getTag();
        if (nBTTagCompound == null) return "";
        return nBTTagCompound.getString("BedWars");
    }

    public org.bukkit.inventory.ItemStack setSkullOwner(org.bukkit.inventory.ItemStack paramItemStack, Player paramPlayer) {
        if (paramItemStack.getType() != Material.SKULL_ITEM)
            return paramItemStack;
        SkullMeta skullMeta = (SkullMeta)paramItemStack.getItemMeta();
        skullMeta.setOwner(paramPlayer.getName());
        paramItemStack.setItemMeta(skullMeta);
        return paramItemStack;
    }

    public org.bukkit.inventory.ItemStack colourItem(org.bukkit.inventory.ItemStack paramItemStack, ITeam paramITeam) {
        if (paramItemStack == null) return null;
        switch (paramItemStack.getType().toString()) {
            default:
                return paramItemStack;
            case "WOOL":
            case "STAINED_CLAY":
            case "STAINED_GLASS":
                return new org.bukkit.inventory.ItemStack(paramItemStack.getType(), paramItemStack.getAmount(), paramITeam.getColor().itemByte());
            case "GLASS":
                break;
        }
        return new org.bukkit.inventory.ItemStack(Material.STAINED_GLASS, paramItemStack.getAmount(), paramITeam.getColor().itemByte());
    }

    public org.bukkit.inventory.ItemStack createItemStack(String paramString, int paramInt, short paramShort) {
        org.bukkit.inventory.ItemStack itemStack;
        try {
            itemStack = new org.bukkit.inventory.ItemStack(Material.valueOf(paramString), paramInt, paramShort);
        } catch (Exception exception) {
            getPlugin().getLogger().log(Level.WARNING, paramString + " no es un material válido: " + getName());
            itemStack = new org.bukkit.inventory.ItemStack(Material.BEDROCK);
        }
        return itemStack;
    }

    public boolean isPlayerHead(String paramString, int paramInt) {
        return (paramString.equals("SKULL_ITEM") && paramInt == 3);
    }

    public Material materialFireball() {
        return Material.FIREBALL;
    }

    public Material materialPlayerHead() {
        return Material.SKULL_ITEM;
    }

    public Material materialSnowball() {
        return Material.SNOW_BALL;
    }

    public Material materialGoldenHelmet() {
        return Material.GOLD_HELMET;
    }

    public Material materialGoldenChestPlate() {
        return Material.GOLD_CHESTPLATE;
    }

    public Material materialGoldenLeggings() {
        return Material.GOLD_LEGGINGS;
    }

    public Material materialCake() {
        return Material.CAKE_BLOCK;
    }

    public boolean isBed(Material paramMaterial) {
        return (paramMaterial == Material.BED_BLOCK || paramMaterial == Material.BED);
    }

    public boolean itemStackDataCompare(org.bukkit.inventory.ItemStack paramItemStack, short paramShort) {
        return (paramItemStack.getData().getData() == paramShort);
    }

    public Material woolMaterial() {
        return Material.WOOL;
    }

    public String getShopUpgradeIdentifier(org.bukkit.inventory.ItemStack paramItemStack) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(paramItemStack);
        NBTTagCompound nBTTagCompound = itemStack.getTag();
        return (nBTTagCompound == null) ? "" : (nBTTagCompound.hasKey("tierIdentifier") ? nBTTagCompound.getString("tierIdentifier") : "");
    }

    public org.bukkit.inventory.ItemStack setShopUpgradeIdentifier(org.bukkit.inventory.ItemStack paramItemStack, String paramString) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(paramItemStack);
        NBTTagCompound nBTTagCompound = itemStack.getTag();
        if (nBTTagCompound == null) {
            nBTTagCompound = new NBTTagCompound();
            itemStack.setTag(nBTTagCompound);
        }
        nBTTagCompound.setString("tierIdentifier", paramString);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    public void sendPlayerSpawnPackets(Player paramPlayer, IArena paramIArena) {
        if (paramPlayer == null) return;
        if (paramIArena == null) return;
        if (!paramIArena.isPlayer(paramPlayer)) return;
        if (paramIArena.getRespawnSessions().containsKey(paramPlayer)) return;
        EntityPlayer entityPlayer = ((CraftPlayer)paramPlayer).getHandle();
        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        PacketPlayOutEntityVelocity packetPlayOutEntityVelocity = new PacketPlayOutEntityVelocity(entityPlayer);
        PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(entityPlayer, getCompressedAngle(entityPlayer.yaw));
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment1 = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 0, entityPlayer.inventory.getItemInHand());
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment2 = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 4, entityPlayer.inventory.getArmorContents()[3]);
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment3 = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 3, entityPlayer.inventory.getArmorContents()[2]);
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment4 = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 2, entityPlayer.inventory.getArmorContents()[1]);
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment5 = new PacketPlayOutEntityEquipment(entityPlayer.getId(), 1, entityPlayer.inventory.getArmorContents()[0]);
        for (Player player : paramIArena.getPlayers()) {
            if (player == null || player.equals(paramPlayer)) continue;
            if (paramIArena.getRespawnSessions().containsKey(player)) continue;
            EntityPlayer entityPlayer1 = ((CraftPlayer)player).getHandle();
            if (player.getWorld().equals(paramPlayer.getWorld()) && paramPlayer.getLocation().distance(player.getLocation()) <= paramIArena.getRenderDistance()) {
                entityPlayer1.playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
                entityPlayer1.playerConnection.sendPacket(packetPlayOutEntityVelocity);
                for (Packet packet : new Packet[] {packetPlayOutEntityEquipment1, packetPlayOutEntityEquipment2, packetPlayOutEntityEquipment3, packetPlayOutEntityEquipment4, packetPlayOutEntityEquipment5, packetPlayOutEntityHeadRotation}) entityPlayer1.playerConnection.sendPacket(packet);
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    hideArmor(player, paramPlayer);
                    continue;
                }
                PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn1 = new PacketPlayOutNamedEntitySpawn(entityPlayer1);
                PacketPlayOutEntityVelocity packetPlayOutEntityVelocity1 = new PacketPlayOutEntityVelocity(entityPlayer1);
                PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation1 = new PacketPlayOutEntityHeadRotation(entityPlayer1, getCompressedAngle(entityPlayer1.yaw));
                entityPlayer.playerConnection.sendPacket(packetPlayOutNamedEntitySpawn1);
                entityPlayer.playerConnection.sendPacket(packetPlayOutEntityVelocity1);
                entityPlayer.playerConnection.sendPacket(packetPlayOutEntityHeadRotation1);
                showArmor(player, paramPlayer);
            }
        }
        for (Player player : paramIArena.getSpectators()) {
            if (player == null || player.equals(paramPlayer)) continue;
            EntityPlayer entityPlayer1 = ((CraftPlayer)player).getHandle();
            paramPlayer.hidePlayer(player);
            if (player.getWorld().equals(paramPlayer.getWorld()) && paramPlayer.getLocation().distance(player.getLocation()) <= 12) {
                entityPlayer1.playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
                entityPlayer1.playerConnection.sendPacket(packetPlayOutEntityVelocity);
                entityPlayer1.playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, getCompressedAngle(entityPlayer.yaw)));
                for (Packet packet : new Packet[] {packetPlayOutEntityEquipment1, packetPlayOutEntityEquipment2, packetPlayOutEntityEquipment3, packetPlayOutEntityEquipment4, packetPlayOutEntityEquipment5}) entityPlayer1.playerConnection.sendPacket(packet);
            }
        }
    }

    public String getInventoryName(InventoryEvent paramInventoryEvent) {
        return paramInventoryEvent.getInventory().getName();
    }

    public void setUnbreakable(ItemMeta paramItemMeta) {
        paramItemMeta.spigot().setUnbreakable(true);
    }

    public int getVersion() {
        return 0;
    }

    public void registerVersionListeners() {
        new VersionCommon();
    }

    public Fireball setFireballDirection(Fireball paramFireball, Vector paramVector) {
        EntityFireball entityFireball = ((CraftFireball)paramFireball).getHandle();
        entityFireball.dirX = paramVector.getX() * 0.1D;
        entityFireball.dirY = paramVector.getY() * 0.1D;
        entityFireball.dirZ = paramVector.getZ() * 0.1D;
        return (Fireball)entityFireball.getBukkitEntity();
    }

    public void playRedStoneDot(Player paramPlayer) {
        paramPlayer.playEffect(EntityEffect.VILLAGER_ANGRY);
    }
}
