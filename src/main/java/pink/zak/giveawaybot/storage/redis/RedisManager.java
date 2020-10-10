package pink.zak.giveawaybot.storage.redis;

import pink.zak.giveawaybot.GiveawayBot;
import redis.clients.jedis.Jedis;

public class RedisManager {
    private final Jedis connection;

    public RedisManager(GiveawayBot bot) {
        /*Config config = bot.getConfigStore().getConfig("settings");
        this.connection = new Jedis(config.string("redis-host"), config.integer("redis-port"), config.bool("redis-use-ssl"));
        this.connection.auth(config.string("redis-password"));

        this.connection.set("test", "testValue");
        System.out.println("Test? " + this.connection.get("test"));*/
        this.connection = null;
    }

    public Jedis getConnection() {
        return this.connection;
    }

    public void shutdown() {
        this.connection.close();
    }
}
