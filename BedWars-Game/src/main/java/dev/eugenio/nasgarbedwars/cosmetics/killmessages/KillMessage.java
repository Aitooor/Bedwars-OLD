package dev.eugenio.nasgarbedwars.cosmetics.killmessages;

import lombok.Getter;

@Getter
public class KillMessage {

    private final String name;
    private final String basic;
    private final String finalKill;
    private final String knockedHighPlace;
    private final String shotByBow;
    private final String blownUp;
    private final String golemandSilverfishKill;
    private final String bedBreak;

    public KillMessage(String name, String basic, String finalKill, String knockedHighPlace, String shotByBow, String blownUp,
                       String golemandSilverfishKill, String bedBreak) {
        this.name = name;
        this.basic = basic;
        this.finalKill = finalKill;
        this.knockedHighPlace = knockedHighPlace;
        this.shotByBow = shotByBow;
        this.blownUp = blownUp;
        this.golemandSilverfishKill = golemandSilverfishKill;
        this.bedBreak = bedBreak;
    }

}
