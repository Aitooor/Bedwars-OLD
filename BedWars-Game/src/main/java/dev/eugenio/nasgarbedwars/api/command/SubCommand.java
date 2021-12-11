package dev.eugenio.nasgarbedwars.api.command;

import dev.eugenio.nasgarbedwars.api.BedWarsAPI;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommand {
    private final String name;
    private final ParentCommand parent;
    private boolean show;
    private int priority;
    private TextComponent displayInfo;
    private boolean arenaSetupCommand;
    private String permission;

    public SubCommand(final ParentCommand parent, final String name) {
        this.show = false;
        this.priority = 20;
        this.arenaSetupCommand = false;
        this.permission = "";
        this.name = name;
        (this.parent = parent).addSubCommand(this);
    }

    public abstract boolean execute(final String[] p0, final CommandSender p1);

    public String getSubCommandName() {
        return this.name;
    }

    public void showInList(final boolean show) {
        this.show = show;
    }

    public ParentCommand getParent() {
        return this.parent;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public TextComponent getDisplayInfo() {
        return this.displayInfo;
    }

    public void setDisplayInfo(final TextComponent displayInfo) {
        this.displayInfo = displayInfo;
    }

    public boolean isArenaSetupCommand() {
        return this.arenaSetupCommand;
    }

    public void setArenaSetupCommand(final boolean arenaSetupCommand) {
        this.arenaSetupCommand = arenaSetupCommand;
    }

    public boolean isShow() {
        return this.show;
    }

    public void setPermission(final String permission) {
        this.permission = permission;
    }

    public boolean hasPermission(final CommandSender commandSender) {
        return this.permission.isEmpty() || commandSender.hasPermission("bw.*") || commandSender.hasPermission(this.permission);
    }

    public boolean canSee(final CommandSender commandSender, final BedWarsAPI bedWarsAPI) {
        return !(commandSender instanceof ConsoleCommandSender) && ((this.isArenaSetupCommand() && bedWarsAPI.isInSetupSession(((Player) commandSender).getUniqueId())) || ((this.isArenaSetupCommand() || !bedWarsAPI.isInSetupSession(((Player) commandSender).getUniqueId())) && !this.isArenaSetupCommand() && this.hasPermission(commandSender)));
    }

    public abstract List<String> getTabComplete();
}
