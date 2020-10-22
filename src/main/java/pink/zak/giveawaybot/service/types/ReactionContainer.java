package pink.zak.giveawaybot.service.types;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.List;

public class ReactionContainer {
    private final MessageReaction.ReactionEmote reactionEmote;

    public static ReactionContainer fromUnknown(String input, Guild guild) {
        if (input.startsWith("<") && input.endsWith(">") && input.contains(":")) {
            String[] split = input.split(":");
            if (split.length != 3 || split[2].length() < 19) {
                return new ReactionContainer((MessageReaction.ReactionEmote) null);
            }
            String id = split[2].substring(0, split[2].length() - 1);
            try {
                return new ReactionContainer(guild.getEmoteById(id));
            } catch (NumberFormatException ex) {
                return new ReactionContainer((MessageReaction.ReactionEmote) null);
            }
        }
        List<String> parsed = EmojiParser.extractEmojis(input);
        if (parsed.size() == 1) {
            return new ReactionContainer(parsed.get(0), guild.getJDA());
        }
        return new ReactionContainer((MessageReaction.ReactionEmote) null);
    }

    public ReactionContainer(String unicode, JDA api) {
        this.reactionEmote = MessageReaction.ReactionEmote.fromUnicode(unicode, api);
    }

    public ReactionContainer(Emote emote) {
        this.reactionEmote = MessageReaction.ReactionEmote.fromCustom(emote);
    }

    public ReactionContainer(MessageReaction.ReactionEmote emote) {
        this.reactionEmote = emote;
    }

    public MessageReaction.ReactionEmote getReactionEmote() {
        return this.reactionEmote;
    }

    @Override
    public String toString() {
        return this.reactionEmote.isEmote() ? this.reactionEmote.getEmote().getAsMention() : this.reactionEmote.getEmoji();
    }
}
