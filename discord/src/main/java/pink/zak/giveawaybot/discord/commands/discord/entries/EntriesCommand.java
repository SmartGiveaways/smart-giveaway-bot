package pink.zak.giveawaybot.discord.commands.discord.entries;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import pink.zak.giveawaybot.discord.GiveawayBot;
import pink.zak.giveawaybot.discord.cache.GiveawayCache;
import pink.zak.giveawaybot.discord.lang.enums.Text;
import pink.zak.giveawaybot.discord.models.Server;
import pink.zak.giveawaybot.discord.models.User;
import pink.zak.giveawaybot.discord.models.giveaway.CurrentGiveaway;
import pink.zak.giveawaybot.discord.service.colour.Palette;
import pink.zak.giveawaybot.discord.service.command.discord.command.SimpleCommand;
import pink.zak.giveawaybot.discord.service.command.discord.command.SubCommand;
import pink.zak.giveawaybot.discord.service.types.UserUtils;

import java.math.BigInteger;
import java.util.List;

public class EntriesCommand extends SimpleCommand {
    private final GiveawayCache giveawayCache;
    private final Palette palette;

    public EntriesCommand(GiveawayBot bot) {
        super(bot, "entries", false, false);
        this.giveawayCache = bot.getGiveawayCache();
        this.palette = bot.getDefaults().getPalette();

        this.setSubCommands(
                new UserEntriesSub(bot)
        );
    }

    @Override
    public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
        this.runLogic(sender, server, event.getChannel(), true);
    }

    private void runLogic(Member target, Server server, TextChannel channel, boolean self) {
        String targetName = UserUtils.getNameDiscrim(target);
        if (server.getActiveGiveaways().isEmpty()) {
            this.langFor(server, Text.NO_ACTIVE_GIVEAWAYS).to(channel);
            return;
        }
        User user = server.getUserCache().get(target.getIdLong());
        if (user.isBanned()) {
            this.langFor(server, self ? Text.SELF_BANNED_FROM_GIVEAWAYS : Text.TARGET_BANNED_FROM_GIVEAWAYS, replacer -> replacer.set("target", target)).to(channel);
            return;
        }
        List<Long> presentGiveaways = server.getActiveGiveaways(user);
        if (presentGiveaways.isEmpty()) {
            this.langFor(server, self ? Text.SELF_NOT_ENTERED : Text.TARGET_NOT_ENTERED, replacer -> replacer.set("target", target)).to(channel);
            return;
        }
        StringBuilder descriptionBuilder = new StringBuilder();
        for (long giveawayId : presentGiveaways) {
            BigInteger entries = user.getEntries(giveawayId);
            CurrentGiveaway giveaway = this.giveawayCache.get(giveawayId);
            if (giveaway != null) {
                descriptionBuilder.append(this.langFor(server,
                        entries.compareTo(BigInteger.ONE) < 1 ? Text.ENTRIES_EMBED_GIVEAWAY_LINE : Text.ENTRIES_EMBED_GIVEAWAY_LINE_PLURAL, replacer -> replacer
                                .set("item", giveaway.getLinkedGiveawayItem())
                                .set("entries", entries.toString())).get());
            }
        }
        channel.sendMessage(new EmbedBuilder()
                .setTitle(this.langFor(server, Text.ENTRIES_EMBED_TITLE, replacer -> replacer.set("target", targetName)).get())
                .setColor(this.palette.primary())
                .setDescription(descriptionBuilder.toString())
                .build()).queue();
    }

    private class UserEntriesSub extends SubCommand {

        public UserEntriesSub(GiveawayBot bot) {
            super(bot, true, false, false);

            this.addArgument(Member.class); // target
        }

        @Override
        public void onExecute(Member sender, Server server, GuildMessageReceivedEvent event, List<String> args) {
            Member target = this.parseArgument(args, event.getGuild(), 0);
            if (target == null) {
                this.langFor(server, Text.COULDNT_FIND_MEMBER).to(event.getChannel());
                return;
            }
            runLogic(target, server, event.getChannel(), false);
        }
    }
}
