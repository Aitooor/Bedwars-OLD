package dev.eugenio.nasgarbedwars.libs.sidebar;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ScoreboardScore;

import java.util.List;

public class CustomScore_v1_8_R3 extends ScoreboardScore {
    private final int score;

    public CustomScore_v1_8_R3(final ScoreboardObjective scoreboardObjective, final String s, final int score) {
        super(null, scoreboardObjective, s);
        this.score = score;
    }

    public static void sendScore(final Sidebar_v1_8_R3 sidebar_v1_8_R3, final String s, final int n) {
        if (sidebar_v1_8_R3.healthObjective == null) return;
        final PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore(new CustomScore_v1_8_R3(sidebar_v1_8_R3.healthObjective, s, n));
        sidebar_v1_8_R3.players.forEach(playerConnection -> playerConnection.sendPacket(packetPlayOutScoreboardScore));
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(final int n) {
    }

    public void updateForList(final List<EntityHuman> list) {
    }

    public void addScore(final int n) {
    }

    public void incrementScore() {
    }
}
