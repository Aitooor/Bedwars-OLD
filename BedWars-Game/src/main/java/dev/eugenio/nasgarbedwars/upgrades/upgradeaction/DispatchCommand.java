package dev.eugenio.nasgarbedwars.upgrades.upgradeaction;

import dev.eugenio.nasgarbedwars.api.arena.team.ITeam;
import dev.eugenio.nasgarbedwars.api.language.Language;
import dev.eugenio.nasgarbedwars.api.upgrades.UpgradeAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class DispatchCommand implements UpgradeAction {
    private final CommandType commandType;
    private final String command;
    
    public DispatchCommand(final CommandType commandType, final String command) {
        this.commandType = commandType;
        this.command = command;
    }
    
    @Override
    public void onBuy(@Nullable final Player player, final ITeam team) {
        this.commandType.dispatch(team, this.command.replace("{buyer}", (player == null) ? "null" : player.getName()).replace("{buyer_uuid}", (player == null) ? "null" : player.getUniqueId().toString()).replace("{team}", team.getName()).replace("{team_display}", team.getDisplayName(Language.getDefaultLanguage())).replace("{team_color}", team.getColor().chat().toString()).replace("{arena}", team.getArena().getArenaName()).replace("{arena_world}", team.getArena().getWorldName()).replace("{arena_display}", team.getArena().getDisplayName()).replace("{arena_group}", team.getArena().getGroup()));
    }
    
    public enum CommandType {
        ONCE_AS_CONSOLE, 
        FOREACH_MEMBER_AS_CONSOLE, 
        FOREACH_MEMBER_AS_PLAYER;
        
        private void dispatch(final ITeam team, String s) {
            switch (this) {
                case ONCE_AS_CONSOLE: {
                    if (s.startsWith("/")) s = s.replaceFirst("/", "");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                    break;
                }
                case FOREACH_MEMBER_AS_CONSOLE: {
                    if (s.startsWith("/")) s = s.replaceFirst("/", "");
                    for (final Player player : team.getMembers()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName()).replace("{player_uuid}", player.getUniqueId().toString()));
                    break;
                }
                case FOREACH_MEMBER_AS_PLAYER: {
                    if (!s.startsWith("/")) s = "/" + s;
                    for (final Player player2 : team.getMembers()) player2.chat(s.replace("{player}", player2.getName()).replace("{player_uuid}", player2.getUniqueId().toString()));
                    break;
                }
            }
        }
    }
}
