package dev.eugenio.nasgarbedwars.commands.bedwars.subcmds.regular;

import dev.eugenio.nasgarbedwars.commands.bedwars.MainCommand;
import dev.eugenio.nasgarbedwars.BedWars;
import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import dev.eugenio.nasgarbedwars.api.arena.team.TeamColor;
import dev.eugenio.nasgarbedwars.api.command.ParentCommand;
import dev.eugenio.nasgarbedwars.api.command.SubCommand;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.language.Messages;
import dev.eugenio.nasgarbedwars.api.server.SetupType;
import dev.eugenio.nasgarbedwars.arena.Arena;
import dev.eugenio.nasgarbedwars.arena.Misc;
import dev.eugenio.nasgarbedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class CmdList extends SubCommand {
    public CmdList(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(11);
        this.showInList(true);
        this.setDisplayInfo(Misc.msgHoverClick("§6 ♦ §e/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + "         §8 - §e ver comandos de jugador", "§aMuestra los comandos disponibles para los jugadores.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }

    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return false;
        final Player player = (Player)commandSender;
        if (SetupSession.isInSetupSession(player.getUniqueId())) {
            final SetupSession session = SetupSession.getSession(player.getUniqueId());
            Objects.requireNonNull(session).getConfig().reload();
            final boolean b = session.getConfig().getYml().get("waiting.Loc") != null;
            final boolean b2 = session.getConfig().getYml().get("waiting.Pos1") != null;
            final boolean b3 = session.getConfig().getYml().get("waiting.Pos2") != null;
            final boolean b4 = b2 && b3;
            final StringBuilder sb = new StringBuilder();
            final StringBuilder sb2 = new StringBuilder();
            final StringBuilder sb3 = new StringBuilder();
            final StringBuilder sb4 = new StringBuilder();
            final StringBuilder sb5 = new StringBuilder();
            final StringBuilder sb6 = new StringBuilder();
            final StringBuilder sb7 = new StringBuilder();
            int n = 0;
            if (session.getConfig().getYml().get("Team") != null) {
                for (final String s : session.getConfig().getYml().getConfigurationSection("Team").getKeys(true)) {
                    if (session.getConfig().getYml().get("Team." + s + ".Color") == null) continue;
                    final ChatColor chatColor = TeamColor.getChatColor(session.getConfig().getYml().getString("Team." + s + ".Color"));
                    if (session.getConfig().getYml().get("Team." + s + ".Spawn") == null) {
                        sb6.append(chatColor).append("\u258b");
                        sb.append(chatColor).append(s).append(" ");
                    }
                    if (session.getConfig().getYml().get("Team." + s + ".Bed") == null) sb2.append(chatColor).append("\u258b");
                    if (session.getConfig().getYml().get("Team." + s + ".Shop") == null) sb3.append(chatColor).append("\u258b");
                    if (session.getConfig().getYml().get("Team." + s + "." + "kill-drops-loc") == null) sb4.append(chatColor).append("\u258b");
                    if (session.getConfig().getYml().get("Team." + s + ".Upgrade") == null) sb5.append(chatColor).append("\u258b");
                    if (session.getConfig().getYml().get("Team." + s + ".Iron") == null || session.getConfig().getYml().get("Team." + s + ".Gold") == null) sb7.append(chatColor).append("\u258b");
                    ++n;
                }
            }
            int size = 0;
            int size2 = 0;
            if (session.getConfig().getYml().get("generator.Emerald") != null) size = session.getConfig().getYml().getStringList("generator.Emerald").size();
            if (session.getConfig().getYml().get("generator.Diamond") != null) size2 = session.getConfig().getYml().getStringList("generator.Diamond").size();
            String s2 = ChatColor.RED + "(NO SETEADO)";
            String s3;
            if (b2 && !b3) {
                s3 = ChatColor.RED + "(POS 2 NO SETEADA)";
            } else if (!b2 && b3) {
                s3 = ChatColor.RED + "(POS 1 NO SETEADA)";
            } else if (b2) {
                s3 = ChatColor.GREEN + "(SETEADO)";
            } else {
                s3 = ChatColor.GRAY + "(NO SETEADO) " + ChatColor.ITALIC + "Opcional";
            }
            final String string = session.getConfig().getYml().getString("group");
            if (string != null && !string.equalsIgnoreCase("default")) s2 = ChatColor.GREEN + "(" + string + ")";
            final int int1 = session.getConfig().getInt("maxInTeam");
            final String string2 = session.dot() + (b ? ChatColor.STRIKETHROUGH : "") + "setWaitingSpawn" + ChatColor.RESET + " " + (b ? (ChatColor.GREEN + "(SETEADO)") : (ChatColor.RED + "(NO SETEADO)"));
            final String string3 = session.dot() + (b4 ? ChatColor.STRIKETHROUGH : "") + "waitingPos 1/2" + ChatColor.RESET + " " + s3;
            final String string4 = session.dot() + ((sb6.length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setSpawn <teamName>" + ChatColor.RESET + " " + ((sb6.length() == 0) ? (ChatColor.GREEN + "(TODO SETEADO)") : (ChatColor.RED + "(Restante: " + sb6 + ChatColor.RED + ")"));
            final String string5 = session.dot() + ((sb2.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setBed" + ChatColor.RESET + " " + ((sb2.length() == 0) ? (ChatColor.GREEN + "(TODO SETEADO)") : (ChatColor.RED + "(Restante: " + sb2 + ChatColor.RED + ")"));
            final String string6 = session.dot() + ((sb3.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setShop" + ChatColor.RESET + " " + ((sb3.length() == 0) ? (ChatColor.GREEN + "(TODO SETEADO)") : (ChatColor.RED + "(Restante: " + sb3 + ChatColor.RED + ")"));
            final String string7 = session.dot() + ((sb4.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setKillDrops" + ChatColor.RESET + " " + ((sb3.length() == 0) ? (ChatColor.GREEN + "(TODO SETEADO)") : (ChatColor.RED + "(Restante: " + sb4 + ChatColor.RED + ")"));
            final String string8 = session.dot() + ((sb5.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setUpgrade" + ChatColor.RESET + " " + ((sb5.length() == 0) ? (ChatColor.GREEN + "(TODO SETEADO)") : (ChatColor.RED + "(Restante: " + sb5 + ChatColor.RED + ")"));
            final String string9 = session.dot() + "addGenerator " + ((sb7.toString().length() == 0) ? "" : (ChatColor.RED + "(Restante: " + sb7 + ChatColor.RED + ") ")) + ChatColor.YELLOW + "(" + ChatColor.DARK_GREEN + "E" + size + " " + ChatColor.AQUA + "D" + size2 + ChatColor.YELLOW + ")";
            final String string10 = session.dot() + ((session.getConfig().getYml().get("spectator-loc") == null) ? "" : ChatColor.STRIKETHROUGH) + "setSpectSpawn" + ChatColor.RESET + " " + ((session.getConfig().getYml().get("spectator-loc") == null) ? (ChatColor.RED + "(NO SETEADO)") : (ChatColor.GRAY + "(SETEADO)"));
            commandSender.sendMessage("");
            commandSender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "." + ChatColor.GOLD + BedWars.getInstance().getDescription().getName() + " v" + BedWars.getInstance().getDescription().getVersion() + ChatColor.GRAY + '-' + " " + ChatColor.GREEN + session.getWorldName() + " comandos");
            player.spigot().sendMessage(Misc.msgHoverClick(string2, ChatColor.WHITE + "Establece el lugar del limbo donde los jugadores\n" + ChatColor.WHITE + "ltienen que esperar hasta que inicie el juego.", "/" + this.getParent().getName() + " setWaitingSpawn", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(string3, ChatColor.WHITE + "Hace que el limbo desaparezca cuando la partida inicie.\n" + ChatColor.WHITE + "Es un cuboide, a si que seleccionalo como si fuera WorldEdit.", "/" + this.getParent().getName() + " waitingPos ", ClickEvent.Action.SUGGEST_COMMAND));
            if (session.getSetupType() == SetupType.ADVANCED) player.spigot().sendMessage(Misc.msgHoverClick(string10, ChatColor.WHITE + "Setea donde deben de aparecer los espectadores.", "/" + this.getParent().getName() + " setSpectSpawn", ClickEvent.Action.RUN_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "autoCreateTeams " + ChatColor.YELLOW + "(auto detectar)", ChatColor.WHITE + "Crea equipos basado en los colores de la islas, puede cometer errores a veces.", "/" + this.getParent().getName() + " autoCreateTeams", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "createTeam <nombre> <color> " + ChatColor.YELLOW + "(" + n + " CREADO)", ChatColor.WHITE + "Crea un equipo.", "/" + this.getParent().getName() + " createTeam ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "removeTeam <name>", ChatColor.WHITE + "Elimina un equipo por el nombre.", "/bw" + " removeTeam ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(string4, ChatColor.WHITE + "Setea el spawn de un equipo.\n" + ChatColor.WHITE + "Equipos sin spawn seteado:\n" + sb.toString(), "/" + this.getParent().getName() + " setSpawn ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(string5, ChatColor.WHITE + "Setea la localizacion de la cama del equipo.\n" + ChatColor.WHITE + "No tienes que especificar el nombre del equipo.", "/" + this.getParent().getName() + " setBed", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(string6, ChatColor.WHITE + "Setea el NPC de compras del equipo.\n" + ChatColor.WHITE + "No tienes que especificar el nombre del equipo.\n" + ChatColor.WHITE + "Solo será spawneado cuando el juego comience.", "/" + this.getParent().getName() + " setShop", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(string8, ChatColor.WHITE + "Setea el NPC de mejoras del equipo.\n" + ChatColor.WHITE + "No tienes que especificar el nombre del equipo.\n" + ChatColor.WHITE + "Solo será spawneado cuando el juego comience.", "/" + this.getParent().getName() + " setUpgrade", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            if (session.getSetupType() == SetupType.ADVANCED) player.spigot().sendMessage(Misc.msgHoverClick(string7, ChatColor.WHITE + "Selecciona una localización donde dropear el contenido del ender chest \n" + ChatColor.WHITE + "del enemigo después de que lo mates y no tenga cama.", "/" + this.getParent().getName() + " setKillDrops ", ClickEvent.Action.SUGGEST_COMMAND));
            final String string11 = ((session.getSetupType() == SetupType.ADVANCED) ? (ChatColor.WHITE + "Añade un generador.\n" + ChatColor.YELLOW + "/" + this.getParent().getName() + " addGenerator <Iron / Gold / Emerald / Diamond>") : (ChatColor.WHITE + "Spawnea un generador.\n" + ChatColor.YELLOW + "Estate en una isla para establecer el generador.")) + "\n" + ChatColor.WHITE + "Estate en un bloque de diamante para poner un generador de diamante.\n" + ChatColor.WHITE + "SEstate en un bloque de esmeralda para poner un generador de esmeralda.";
            player.spigot().sendMessage(Misc.msgHoverClick(string9, string11, "/" + this.getParent().getName() + " addGenerator ", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "removeGenerator", string11, "/" + this.getParent().getName() + " removeGenerator", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            if (session.getSetupType() == SetupType.ADVANCED) {
                player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "setMaxInTeam <int> (IS SET TO " + int1 + ")", ChatColor.WHITE + "Establece la capacidad máxima de jugadores del equipo.", "/bw" + " setMaxInTeam ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "arenaGroup " + s2, ChatColor.WHITE + "Setea el grupo de arena.", "/bw" + " arenaGroup ", ClickEvent.Action.SUGGEST_COMMAND));
            } else {
                player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "setType <type> " + s2, ChatColor.WHITE + "Añadir arena al grupo.", "/" + this.getParent().getName() + " setType", ClickEvent.Action.RUN_COMMAND));
            }
            player.spigot().sendMessage(Misc.msgHoverClick(session.dot() + "save", ChatColor.WHITE + "Guarda la arena y te lleva al lobby.", "/" + this.getParent().getName() + " save", ClickEvent.Action.SUGGEST_COMMAND));
        } else {
            if (!player.hasPermission("bedwars.admin")) {
                player.sendMessage("§aNasgarBedwars, versión v" + BedWars.getInstance().getDescription().getVersion());
                return false;
            }

            for (String s : Language.getList((Player) commandSender, Messages.COMMAND_MAIN)) commandSender.sendMessage(s);
        }
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        return null;
    }

    @Override
    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        if (commandSender instanceof Player) {
            final Player player = (Player)commandSender;
            if (Arena.isInArena(player)) return false;
            if (SetupSession.isInSetupSession(player.getUniqueId())) return false;
        }
        return this.hasPermission(commandSender);
    }
}