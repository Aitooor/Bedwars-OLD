package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.sensitive;

import dev.eugenio.nasgarbedwars.configuration.LevelsConfig;
import dev.eugenio.nasgarbedwars.configuration.Permissions;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.events.player.PlayerXPGainEvent;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Level extends SubCommand {
    public Level(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPermission(Permissions.PERMISSION_LEVEL);
        this.setPriority(10);
        this.showInList(true);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §8      - §eclick para detalles", "§fManeja el nivel del jugador.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (array.length == 0) {
            sendSubCommands(commandSender);
            return true;
        }
        if (array[0].equalsIgnoreCase("setlevel")) {
            int i;
            if (array.length != 3) {
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "Uso: /bw level setLevel <nivel");
                return true;
            }
            Player player = Bukkit.getPlayer(array[1]);
            if (player == null) {
                commandSender.sendMessage(ChatColor.RED + " \u25aa " + ChatColor.GRAY + "Jugador no encontrado.");
                return true;
            }
            try {
                i = Integer.parseInt(array[2]);
            } catch (Exception exception) {
                commandSender.sendMessage(ChatColor.RED + "El nivel debe de ser un número.");
                return true;
            }
            BedWars.getInstance().getApi().getLevelsUtil().setLevel(player, i);
            int j = (LevelsConfig.levels.getYml().get("levels." + i + ".rankup-cost") == null) ? LevelsConfig.levels.getInt("levels.others.rankup-cost") : LevelsConfig.levels.getInt("levels." + i + ".rankup-cost");
            String str = (LevelsConfig.levels.getYml().get("levels." + i + ".name") == null) ? LevelsConfig.levels.getYml().getString("levels.others.name") : LevelsConfig.levels.getYml().getString("levels." + i + ".name");
            BedWars.getInstance().getServer().getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
                BedWars.getInstance().getMySQLDatabase().setLevelData(player.getUniqueId(), i, 0, str, j);
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + player.getName() + " fue puesto a nivel: " + array[2]);
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "El jugador quizá necesite volver a unirse para verlo actualizado.");
            });
        } else if (array[0].equalsIgnoreCase("givexp")) {
            int i;
            if (array.length != 3) {
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "Uso: /bw level giveXp <jugador> <cantidad>");
                return true;
            }
            Player player = Bukkit.getPlayer(array[1]);
            if (player == null) {
                commandSender.sendMessage(ChatColor.RED + " \u25aa " + ChatColor.GRAY + "Jugador no encontrado.");
                return true;
            }
            try {
                i = Integer.parseInt(array[2]);
            } catch (Exception exception) {
                commandSender.sendMessage(ChatColor.RED + "La cantidad debe de ser un número.");
                return true;
            }
            BedWars.getInstance().getApi().getLevelsUtil().addXp(player, i, PlayerXPGainEvent.XpSource.OTHER);
            BedWars.getInstance().getServer().getScheduler().runTaskAsynchronously(BedWars.getInstance(), () -> {
                Object[] arrayOfObject = BedWars.getInstance().getMySQLDatabase().getLevelData(player.getUniqueId());
                BedWars.getInstance().getMySQLDatabase().setLevelData(player.getUniqueId(), (Integer) arrayOfObject[0], (Integer) arrayOfObject[1] + i, (String)arrayOfObject[2], (Integer) arrayOfObject[3]);
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + array[2] + " XP fue dada a: " + player.getName());
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "El jugador quizá necesite volver a unirse para verlo actualizado.");
            });
        } else {
            sendSubCommands(commandSender);
        }
        return true;
    }
    
    private void sendSubCommands(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            final Player player = (Player)commandSender;
            player.spigot().sendMessage(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " setLevel §o<jugador> <nivel>", "Setea el nivel de un jugador.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " setLevel", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " giveXp §o<jugador> <nivel>", "Givea experiencia a un jugador.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " giveXp", ClickEvent.Action.SUGGEST_COMMAND));
        } else {
            commandSender.sendMessage(ChatColor.GOLD + "bw level setLevel <jugador> <nivel>");
            commandSender.sendMessage(ChatColor.GOLD + "bw level giveXp <jugador> <nivel>");
        }
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
