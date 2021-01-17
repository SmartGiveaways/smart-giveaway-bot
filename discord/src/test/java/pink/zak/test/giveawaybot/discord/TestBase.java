package pink.zak.test.giveawaybot.discord;

import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.SelfUserImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import net.dv8tion.jda.internal.utils.config.MetaConfig;
import net.dv8tion.jda.internal.utils.config.flags.ConfigFlag;
import pink.zak.giveawaybot.discord.threads.ThreadManager;
import pink.zak.giveawaybot.discord.threads.ThreadManagerImpl;

import java.util.EnumSet;

public class TestBase {

    public static final JDAImpl JDA_MOCK = new JDAImpl(
            new AuthorizationConfig("aa"), null, null,
            new MetaConfig(2048, null, EnumSet.noneOf(CacheFlag.class), ConfigFlag.getDefault())
    );
    public static final ThreadManager THREAD_MANAGER = new ThreadManagerImpl();

    static {
        JDA_MOCK.setSelfUser(new SelfUserImpl(751886759503069287L, JDA_MOCK));
    }
}
