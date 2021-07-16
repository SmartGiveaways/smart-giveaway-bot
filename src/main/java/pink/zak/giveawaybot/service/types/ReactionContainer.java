package pink.zak.giveawaybot.service.types;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import java.util.List;

public class ReactionContainer {
    private MessageReaction.ReactionEmote reactionEmote;

    public ReactionContainer(String unicode, JDA api) {
        this.reactionEmote = MessageReaction.ReactionEmote.fromUnicode(unicode, api);
    }

    public ReactionContainer(Emote emote) {
        try {
            this.reactionEmote = MessageReaction.ReactionEmote.fromCustom(emote);
        } catch (NullPointerException ex) {
            this.reactionEmote = null;
        }
    }

    public static ReactionContainer fromUnknown(String input, Guild guild) {
        if (input.startsWith("<") && input.endsWith(">") && input.contains(":")) {
            String[] split = input.split(":");
            if (split.length != 3 || split[1].isEmpty() || split[2].length() < 19) {
                return null;
            }
            String id = split[2].substring(0, split[2].length() - 1);
            try {
                Emote possibleEmote = guild.retrieveEmoteById(id).complete(true);
                return possibleEmote == null ? null : new ReactionContainer(possibleEmote);
            } catch (IllegalArgumentException | RateLimitedException ex) {
                return null;
            }
        }
        List<String> parsed = EmojiParser.extractEmojis(input);
        if (parsed.size() == 1) {
            return new ReactionContainer(parsed.get(0), guild.getJDA());
        }
        return null;
    }

    public MessageReaction.ReactionEmote getReactionEmote() {
        return this.reactionEmote;
    }

    @Override
    public String toString() {
        return this.reactionEmote.isEmote() ? this.reactionEmote.getEmote().getAsMention() : this.reactionEmote.getEmoji();
    }
}
