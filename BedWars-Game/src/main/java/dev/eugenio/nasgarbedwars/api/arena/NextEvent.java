package dev.eugenio.nasgarbedwars.api.arena;

public enum NextEvent {
    DIAMOND_GENERATOR_TIER_II("next-event.diamond-II"),
    DIAMOND_GENERATOR_TIER_III("next-event.diamond-III"),
    EMERALD_GENERATOR_TIER_II("next-event.emerald-II"),
    EMERALD_GENERATOR_TIER_III("next-event.emerald-III"),
    BEDS_DESTROY("next-event.beds-destroy"),
    ENDER_DRAGON("next-event.dragons-spawn"),
    GAME_END("next-event.game-end");

    private final String soundPath;

    NextEvent(final String soundPath) {
        this.soundPath = soundPath;
    }

    public String getSoundPath() {
        return this.soundPath;
    }
}
