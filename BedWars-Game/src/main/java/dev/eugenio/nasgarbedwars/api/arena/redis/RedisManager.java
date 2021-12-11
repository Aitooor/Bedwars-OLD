package dev.eugenio.nasgarbedwars.api.arena.redis;

import dev.eugenio.nasgarbedwars.BedWars;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
    @Getter
    private JedisPool pool;

    public void initialize() {
        pool = new JedisPool(new JedisPoolConfig(), BedWars.getInstance().getConfig().getString("Redis.Address"), BedWars.getInstance().getConfig().getInt("Redis.Port"), 3000, BedWars.getInstance().getConfig().getString("Redis.Password"));
    }

    public void shutdown() {
        if (pool != null) {
            pool.getResource().shutdown();
            pool.destroy();
        }
    }
}