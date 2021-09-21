package pink.zak.giveawaybot.service.command.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pink.zak.giveawaybot.GiveawayBot;
import pink.zak.giveawaybot.data.models.Server;

public abstract class BotSubCommand extends GenericBotCommand {
    private final String subCommandId;
    private final String subCommandGroupId;

    protected BotSubCommand(GiveawayBot bot, String subCommandId, boolean manager, boolean premium) {
        super(bot, manager, premium);
        this.subCommandId = subCommandId;
        this.subCommandGroupId = null;
    }

    protected BotSubCommand(GiveawayBot bot, String subCommandGroupId, String subCommandId, boolean manager, boolean premium) {
        super(bot, manager, premium);
        this.subCommandId = subCommandId;
        this.subCommandGroupId = subCommandGroupId;
    }

    public String getSubCommandId() {
        return this.subCommandId;
    }

    public String getSubCommandGroupId() {
        return this.subCommandGroupId;
    }

    @Override
    public abstract void onExecute(Member sender, Server server, SlashCommandEvent event);
}