package pink.zak.giveawaybot.lang.enums;

public enum Text {
    // Applies everywhere
    BOT_DOESNT_HAVE_PERMISSIONS,
    NO_PERMISSION,
    COULDNT_FIND_MEMBER,
    NO_ACTIVE_GIVEAWAYS, // Might be moved to entries
    FATAL_ERROR_LOADING_SERVER,
    GENERIC_FAILURE,
    GENERIC_EMBED_FOOTER,
    COULDNT_FIND_CHANNEL,
    COULDNT_FIND_GIVEAWAY,


    // Applies to both ban commands
    CANNOT_BAN_SELF,
    NOT_ENOUGH_PERMISSIONS_BAN,


    // Applies to >gban
    BAN_HELP,
    TARGET_ALREADY_BANNED,
    CANNOT_BAN_IS_SHADOW_BANNED, // You cannot ban someone normally if they are shadow banned
    BANNED_SUCCESSFULLY,


    // Applies to >gsban
    SHADOW_BAN_HELP,
    TARGET_ALREADY_SHADOW_BANNED,
    CANNOT_BAN_IS_BANNED, // You cannot shadow ban someone if they are banned normally.
    SHADOW_BANNED_SUCCESSFULLY,


    // Applies to >gunban
    UNBAN_HELP,
    CANNOT_UNBAN_SELF,
    UNBAN_NOT_BANNED,
    SHADOW_UNBANNED,
    UNBANNED,

    // Applies to >entries
    SELF_BANNED_FROM_GIVEAWAYS,
    TARGET_BANNED_FROM_GIVEAWAYS,
    SELF_NOT_ENTERED,
    TARGET_NOT_ENTERED,
    // Contentful
    ENTRIES_EMBED_TITLE,
    ENTRIES_EMBED_GIVEAWAY_LINE, // This is what is used for every line. One giveaway per line.
    ENTRIES_EMBED_GIVEAWAY_LINE_PLURAL,


    // Applies to >giveaway commands
    GIVEAWAY_EMBED_TITLE,
    GIVEAWAY_EMBED_FOOTER,
    GIVEAWAY_EMBED_CONTENT,
    //
    GIVEAWAY_LIST_EMBED_TITLE,
    // creating
    GIVEAWAY_LIMIT_FAILURE,
    NO_PRESET_FOUND_ON_CREATION,
    UNKNOWN_EMOJI_ON_CREATION,
    GIVEAWAY_CREATED,
    GIVEAWAY_LENGTH_TOO_SHORT,
    GIVEAWAY_LENGTH_TOO_LONG,
    WINNER_AMOUNT_TOO_LARGE,
    WINNER_AMOUNT_TOO_SMALL,
    PARSING_REWARD_FAILED,
    // reroll
    REROLL_OVER_24_HOURS,


    // Applies to >ghelp commands
    HELP_EMBED_TITLE,
    HELP_EMBED_FOOTER,
    HELP_LIMITED_SECTION,
    HELP_ADMIN_SECTION,

    // Applies to >preset commands
    PRESET_EMBED_TITLE,
    PRESET_EMBED_CONTENT
    ;

    public static Text match(String identifier) {
        return Text.valueOf(identifier.toUpperCase().replace("-", "_"));
    }
}
