package pink.zak.test.giveawaybot.discord.service.types;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import pink.zak.giveawaybot.discord.service.BotConstants;
import pink.zak.giveawaybot.discord.service.types.ReactionContainer;
import pink.zak.test.giveawaybot.discord.TestBase;
import pink.zak.test.giveawaybot.discord.fakes.FakeRestAction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReactionContainerTests {

    @Test
    void testFromUnicode() {
        assertEquals(BotConstants.BACK_ARROW, new ReactionContainer(BotConstants.BACK_ARROW, TestBase.JDA_MOCK).getReactionEmote().getEmoji());
    }

    @Test
    void testFromUnknown() {
        FakeReactionGuild guild = new FakeReactionGuild();
        Emote emote = new EmoteImpl(774238713025921045L, guild, true).setName("communism");
        SnowflakeCacheViewImpl<Emote> emoteView = ((SnowflakeCacheViewImpl<Emote>) guild.getEmoteCache());
        try (UnlockHook hook = emoteView.writeLock()) {
            emoteView.getMap().put(774238713025921045L, emote);
        }

        assertNull(ReactionContainer.fromUnknown("<774238713025921045>", guild)); // Bad format
        assertNull(ReactionContainer.fromUnknown("<::774238713025921045>", guild)); // Bad format
        assertNull(ReactionContainer.fromUnknown(":communism:774238713025921045>", guild)); // Missing <
        assertNull(ReactionContainer.fromUnknown("<:communism:774238713025921045", guild)); // Missing >
        assertNull(ReactionContainer.fromUnknown("<something:774238713025921045>", guild)); // Not enough :s
        assertNull(ReactionContainer.fromUnknown("<:something:77423871302592104>", guild)); // Snowflake length too short
        assertNull(new ReactionContainer(null).getReactionEmote());

        assertNull(ReactionContainer.fromUnknown("<:something:563459102626497271>", guild)); // Not found in cache
        assertNull(ReactionContainer.fromUnknown("<:something:563459102626497271999>", guild)); // Snowflake length too long

        ReactionContainer emojiContainer = ReactionContainer.fromUnknown(BotConstants.BACK_ARROW, guild);
        ReactionContainer emoteContainer = ReactionContainer.fromUnknown("<:communism:774238713025921045>", guild);
        assertNotNull(emojiContainer);
        assertNotNull(emoteContainer);
        assertEquals(BotConstants.BACK_ARROW, emojiContainer.getReactionEmote().getEmoji());
        assertEquals(BotConstants.BACK_ARROW, emojiContainer.toString());
        assertEquals("<:communism:774238713025921045>", emoteContainer.toString());
    }

    static class FakeReactionGuild extends GuildImpl {

        public FakeReactionGuild() {
            super(TestBase.JDA_MOCK, 751886048623067186L);
        }

        @NotNull
        @Override
        public RestAction<ListedEmote> retrieveEmoteById(@NotNull String id) {
            Checks.isSnowflake(id, "Emote ID");

            Emote emote = getEmoteById(id);
            if (emote != null) {
                ListedEmote listedEmote = (ListedEmote) emote;
                return new FakeRestAction<>(listedEmote);
            }
            return new FakeRestAction<>(null);
        }
    }
}
