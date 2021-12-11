package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive.setup;

import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoCreateTeams extends SubCommand {
    private static HashMap<Player, Long> timeOut;
    private static HashMap<Player, List<Byte>> teamsFoundOld;
    private static HashMap<Player, List<String>> teamsFound13;
    
    public AutoCreateTeams(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) {
            commandSender.sendMessage("§c ♦ §7No estás en una sesión de setup.");
            return true;
        }
        if (session.getSetupType() == SetupType.ASSISTED) {
            if (is13Higher()) {
                if (AutoCreateTeams.timeOut.containsKey(player) && AutoCreateTeams.timeOut.get(player) >= System.currentTimeMillis() && AutoCreateTeams.teamsFound13.containsKey(player)) {
                    for (final String s : AutoCreateTeams.teamsFound13.get(player)) Bukkit.dispatchCommand(commandSender, "bw" + " createTeam " + TeamColor.enName(s) + " " + TeamColor.enName(s));
                    if (session.getConfig().getYml().get("waiting.Pos1") == null) {
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§e§lELIMINACIÓN DE LIMBO");
                        commandSender.sendMessage("§aSi quieres que el pre-lobby al iniciar la partida se elimine,");
                        commandSender.sendMessage("§apor favor, usa los siguientes comandos para establecer sus posiciones.");
                        player.spigot().sendMessage(Misc.msgHoverClick("§c ♦ §7/" + "bw" + " waitingPos 1", "§dSetear pos. 1", "/" + this.getParent().getName() + " waitingPos 1", ClickEvent.Action.RUN_COMMAND));
                        player.spigot().sendMessage(Misc.msgHoverClick("§c ♦ §7/" + "bw" + " waitingPos 2", "§dSetear pos. 2", "/" + this.getParent().getName() + " waitingPos 2", ClickEvent.Action.RUN_COMMAND));
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§7Esto es opcional, pero no me seas cutre y hazlo xd. Si igualmente te da igual, usa §6/bw §7para saltartelo.");
                    }
                    return true;
                }
                final ArrayList<String> list = new ArrayList<>();
                final World world = player.getWorld();
                if (session.getConfig().getYml().get("Team") == null) {
                    player.sendMessage("§6 ♦ §7Buscando equipos. §cEsto va a causar lag, no te preocupes.");
                    for (int i = -200; i < 200; ++i) {
                        for (int j = 50; j < 130; ++j) {
                            for (int k = -200; k < 200; ++k) {
                                final Block block = new Location(world, i, j, k).getBlock();
                                if (block.getType().toString().contains("_WOOL") && !list.contains(block.getType().toString())) {
                                    int n = 0;
                                    for (int l = -2; l < 2; ++l) for (int n2 = -2; n2 < 2; ++n2) for (int n3 = -2; n3 < 2; ++n3) if (new Location(world, i, j, k).getBlock().getType() == block.getType()) ++n;
                                    if (n >= 5 && !TeamColor.enName(block.getType().toString()).isEmpty() && session.getConfig().getYml().get("Team." + TeamColor.enName(block.getType().toString())) == null) list.add(block.getType().toString());
                                }
                            }
                        }
                    }
                }
                if (list.isEmpty()) {
                    player.sendMessage("§6 ♦ §7No se han encontrado nuevos equipos.\n§6 ♦ §7Crealos manualmente con §6/" + "bw" + " createTeam");
                } else {
                    if (AutoCreateTeams.timeOut.containsKey(player)) {
                        player.sendMessage("§c ♦ §7Timeout. Inténtalo de nuevo.");
                        AutoCreateTeams.timeOut.remove(player);
                        return true;
                    }
                    AutoCreateTeams.timeOut.put(player, System.currentTimeMillis() + 16000L);
                    if (AutoCreateTeams.teamsFound13.containsKey(player)) {
                        AutoCreateTeams.teamsFound13.replace(player, list);
                    } else {
                        AutoCreateTeams.teamsFound13.put(player, list);
                    }
                    player.sendMessage("§6§lNUEVOS EQUIPOS ENCONTRADOS:");
                    for (String s : list) {
                        final String enName = TeamColor.enName(s);
                        player.sendMessage("§a ♦ " + TeamColor.getChatColor(enName) + enName);
                    }
                    player.spigot().sendMessage(Misc.msgHoverClick("§6 ♦ §7§lClick aquí para encontrar los equipos detectados.", "§fClick para crear los equipos encontrados.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
                }
            } else {
                if (AutoCreateTeams.timeOut.containsKey(player) && AutoCreateTeams.timeOut.get(player) >= System.currentTimeMillis() && AutoCreateTeams.teamsFoundOld.containsKey(player)) {
                    for (final Byte b : AutoCreateTeams.teamsFoundOld.get(player)) {
                        Bukkit.dispatchCommand(commandSender, "bw" + " createTeam " + TeamColor.enName(b) + " " + TeamColor.enName(b));
                    }
                    if (session.getConfig().getYml().get("waiting.Pos1") == null) {
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§e§lELIMINACIÓN DE LIMBO");
                        commandSender.sendMessage("§aSi quieres que el pre-lobby al iniciar la partida se elimine,");
                        commandSender.sendMessage("§apor favor, usa los siguientes comandos para establecer sus posiciones.");
                        player.spigot().sendMessage(Misc.msgHoverClick("§c ♦ §7/" + "bw" + " waitingPos 1", "§dSetear pos. 1", "/" + this.getParent().getName() + " waitingPos 1", ClickEvent.Action.RUN_COMMAND));
                        player.spigot().sendMessage(Misc.msgHoverClick("§c ♦ §7/" + "bw" + " waitingPos 2", "§dSetear pos. 2", "/" + this.getParent().getName() + " waitingPos 2", ClickEvent.Action.RUN_COMMAND));
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§7Esto es opcional, pero no me seas cutre y hazlo xd. Si igualmente te da igual, usa §6/bw §7para saltartelo.");
                    }
                    return true;
                }
                final ArrayList<Byte> list2 = new ArrayList<>();
                final World world2 = player.getWorld();
                if (session.getConfig().getYml().get("Team") == null) {
                    player.sendMessage("§6 ♦ §7Buscando equipos. §cEsto va a causar lag, no te preocupes.");
                    for (int n4 = -200; n4 < 200; ++n4) {
                        for (int n5 = 50; n5 < 130; ++n5) {
                            for (int n6 = -200; n6 < 200; ++n6) {
                                final Block block2 = new Location(world2, n4, n5, n6).getBlock();
                                if (block2.getType() == Material.WOOL && !list2.contains(block2.getData())) {
                                    int n7 = 0;
                                    for (int n8 = -2; n8 < 2; ++n8) {
                                        for (int n9 = -2; n9 < 2; ++n9) {
                                            for (int n10 = -2; n10 < 2; ++n10) {
                                                final Block block3 = new Location(world2, n4, n5, n6).getBlock();
                                                if (block3.getType() == block2.getType() && block2.getData() == block3.getData()) ++n7;
                                            }
                                        }
                                    }
                                    if (n7 >= 5 && !TeamColor.enName(block2.getData()).isEmpty() && session.getConfig().getYml().get("Team." + TeamColor.enName(block2.getData())) == null) list2.add(block2.getData());
                                }
                            }
                        }
                    }
                }
                if (list2.isEmpty()) {
                    player.sendMessage("§6 ♦ §7No se han encontrado equipos.\n§6 ♦ §7Crealos manualmente con §6/" + "bw" + " createTeam");
                } else {
                    if (AutoCreateTeams.timeOut.containsKey(player)) {
                        player.sendMessage("§c ♦ §7Timeout. Inténtalo de nuevo.");
                        AutoCreateTeams.timeOut.remove(player);
                        return true;
                    }
                    AutoCreateTeams.timeOut.put(player, System.currentTimeMillis() + 16000L);
                    if (AutoCreateTeams.teamsFoundOld.containsKey(player)) {
                        AutoCreateTeams.teamsFoundOld.replace(player, list2);
                    } else {
                        AutoCreateTeams.teamsFoundOld.put(player, list2);
                    }
                    player.sendMessage("§6§lNUEVOS EQUIPOS ENCONTRADOS:");
                    for (Byte aByte : list2) {
                        final String enName2 = TeamColor.enName(aByte);
                        player.sendMessage("§a ♦ " + TeamColor.getChatColor(enName2) + enName2);
                    }
                    player.spigot().sendMessage(Misc.msgHoverClick("§6 ♦ §7§lClick aquí para encontrar los equipos detectados.", "§fClick para crear los equipos encontrados.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    public static boolean is13Higher() {
        final String serverVersion = BedWars.getInstance().getServer().getVersion();
        switch (serverVersion) {
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1":
                return false;
            default:
                return true;
        }
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
    
    static {
        AutoCreateTeams.timeOut = new HashMap<>();
        AutoCreateTeams.teamsFoundOld = new HashMap<>();
        AutoCreateTeams.teamsFound13 = new HashMap<>();
    }
}
