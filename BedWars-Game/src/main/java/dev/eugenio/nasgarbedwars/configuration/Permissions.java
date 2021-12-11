package dev.eugenio.nasgarbedwars.configuration;

public class Permissions {
    public static final String PERMISSION_FORCESTART;
    public static final String PERMISSION_ALL;
    public static final String PERMISSION_COMMAND_BYPASS;
    public static final String PERMISSION_SETUP_ARENA;
    public static final String PERMISSION_ARENA_GROUP;
    public static final String PERMISSION_BUILD;
    public static final String PERMISSION_CLONE;
    public static final String PERMISSION_DEL_ARENA;
    public static final String PERMISSION_ARENA_ENABLE;
    public static final String PERMISSION_ARENA_DISABLE;
    public static final String PERMISSION_RELOAD;
    public static final String PERMISSION_LEVEL;
    
    static {
        PERMISSION_FORCESTART = "bw.forcestart";
        PERMISSION_ALL = "bw.*";
        PERMISSION_COMMAND_BYPASS = "bw.admin";
        PERMISSION_SETUP_ARENA = "bw.admin";
        PERMISSION_ARENA_GROUP = "bw.admin";
        PERMISSION_BUILD = "bw.admin";
        PERMISSION_CLONE = "bw.admin";
        PERMISSION_DEL_ARENA = "bw.admin";
        PERMISSION_ARENA_ENABLE = "bw.admin";
        PERMISSION_ARENA_DISABLE = "bw.admin";
        PERMISSION_RELOAD = "bw.admin";
        PERMISSION_LEVEL = "bw.admin";
    }
}
