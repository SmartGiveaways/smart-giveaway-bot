package pink.zak.giveawaybot.discord.lang.enums;

public enum Text {
    // Applies everywhere
    BOT_MISSING_PERMISSIONS,
    BOT_MISSING_PERMISSION_SPECIFIC,
    BOT_MISSING_PERMISSIONS_SPECIFIC,
    NO_PERMISSION,
    COULDNT_FIND_MEMBER,
    COULDNT_FIND_ROLE,
    COULDNT_FIND_CHANNEL,
    COULDNT_FIND_GIVEAWAY,
    COULDNT_FIND_SCHEDULED_GIVEAWAY,
    COULDNT_FIND_PRESET,
    COULDNT_FIND_SETTING,
    NO_ACTIVE_GIVEAWAYS,
    NO_SCHEDULED_GIVEAWAYS,
    FATAL_ERROR_LOADING_SERVER,
    GENERIC_COMMAND_USAGE_EXAMPLE,
    GENERIC_FAILURE,
    GENERIC_EMBED_FOOTER,
    COMMAND_REQUIRES_PREMIUM,

    // GuildJoinListener messages
    NEW_GUILD_JOINED,

    // Applies to giveaway messages / embeds
    GIVEAWAY_EMBED_TITLE,
    GIVEAWAY_EMBED_FOOTER_SINGULAR,
    GIVEAWAY_EMBED_FOOTER_PLURAL,
    GIVEAWAY_EMBED_DESCRIPTION_REACTION,
    GIVEAWAY_EMBED_DESCRIPTION_ALL,

    GIVEAWAY_FINISHED_EMBED_DESCRIPTION_NO_WINNERS,
    GIVEAWAY_FINISHED_EMBED_FOOTER_NO_WINNERS,

    GIVEAWAY_FINISHED_EMBED_DESCRIPTION_SINGULAR,
    GIVEAWAY_FINISHED_EMBED_DESCRIPTION_PLURAL,

    GIVEAWAY_FINISHED_EMBED_FOOTER_SINGULAR,
    GIVEAWAY_FINISHED_EMBED_FOOTER_PLURAL,

    GIVEAWAY_FINISHED_WINNER_MESSAGE,
    GIVEAWAY_FINISHED_WINNERS_MESSAGE,
    GIVEAWAY_FINISHED_WINNER_DM,

    // Applies to >gabout commands
    ABOUT_EMBED_TITLE,
    ABOUT_EMBED_CONTENT,


    // Applies to >gadmin commands
    ADMIN_EMBED_TITLE,
    ADMIN_EMBED_CONTENT,

    ADMIN_LIST_LANGUAGES_EMBED_TITLE,
    ADMIN_LIST_LANGUAGES_EMBED_FOOTER,
    ADMIN_LANGUAGE_NOT_FOUND,
    ADMIN_SET_LANGUAGE,

    ADMIN_NO_MANAGERS,
    ADMIN_MANAGER_LIST_TITLE,

    ADMIN_MANAGER_LIMIT_REACHED,
    ADMIN_MANAGER_ALREADY_CONTAINS,
    ADMIN_MANAGER_DOESNT_CONTAIN,
    ADMIN_MANAGER_ROLE_ADDED,
    ADMIN_MANAGER_ROLE_REMOVED,


    // Applies to both ban commands
    CANNOT_BAN_SELF,
    CANNOT_BAN_THE_BOT,
    NOT_ENOUGH_PERMISSIONS_BAN,


    // Applies to >gban
    BAN_EMBED_TITLE,
    BAN_EMBED_CONTENT,
    TARGET_ALREADY_BANNED,
    CANNOT_BAN_IS_SHADOW_BANNED, // You cannot ban someone normally if they are shadowbanned
    BANNED_SUCCESSFULLY,


    // Applies to >gban -s
    TARGET_ALREADY_SHADOW_BANNED,
    CANNOT_BAN_IS_BANNED, // You cannot shadowban someone if they are banned normally.
    SHADOW_BANNED_SUCCESSFULLY,

    // Applies to >gban list
    BAN_LIST_EMBED_TITLE,
    BAN_LIST_PAGE_FOOTER,
    BAN_LIST_FOOTER,
    BAN_LIST_BANNED,
    BAN_LIST_SHADOW_BANNED,

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
    GIVEAWAY_HELP_EMBED_TITLE,
    GIVEAWAY_HELP_EMBED_FOOTER,
    GIVEAWAY_HELP_EMBED_CONTENT,
    //
    GIVEAWAY_LIST_EMBED_TITLE,
    // creating - create sub,
    GIVEAWAY_LIMIT_FAILURE,
    GIVEAWAY_LIMIT_FAILURE_PREMIUM,
    NO_PRESET_FOUND_ON_CREATION,
    UNKNOWN_EMOJI_ON_CREATION,
    GIVEAWAY_CREATED,
    GIVEAWAY_LENGTH_TOO_SHORT,
    GIVEAWAY_LENGTH_TOO_SHORT_PREMIUM,
    GIVEAWAY_LENGTH_TOO_LONG,
    GIVEAWAY_LENGTH_TOO_LONG_PREMIUM,
    WINNER_AMOUNT_TOO_LARGE,
    WINNER_AMOUNT_TOO_SMALL,
    GIVEAWAY_ITEM_TOO_LONG,
    // creating/scheduling - schedule sub
    SCHEDULED_GIVEAWAY_LIST_EMBED_TITLE,
    GIVEAWAY_SCHEDULED,
    SCHEDULED_TIME_TOO_SOON,
    SCHEDULED_TIME_TOO_FAR_AWAY,
    SCHEDULED_GIVEAWAY_LIMIT_FAILURE,
    SCHEDULED_GIVEAWAY_LIMIT_FAILURE_FUTURE,
    // Info Sub
    SCHEDULED_GIVEAWAY_INFO_EMBED_TITLE,
    SCHEDULED_GIVEAWAY_INFO_EMBED_DESCRIPTION,
    CURRENT_GIVEAWAY_INFO_EMBED_TITLE,
    CURRENT_GIVEAWAY_INFO_EMBED_DESCRIPTION,
    FINISHED_GIVEAWAY_INFO_EMBED_TITLE,
    FINISHED_GIVEAWAY_INFO_EMBED_DESCRIPTION,
    // delete sub
    GIVEAWAY_DELETED,
    SCHEDULED_GIVEAWAY_DELETED,
    // history sub
    GIVEAWAY_HISTORY_EMBED_TITLE,
    GIVEAWAY_HISTORY_EMBED_LINE,
    GIVEAWAY_HISTORY_EMBED_FOOTER,
    // reroll
    REROLL_OVER_24_HOURS,
    REROLL_ONE_WINNER,
    REROLL_MULTIPLE_WINNERS,


    // Applies to >ghelp commands
    HELP_EMBED_TITLE,
    HELP_EMBED_FOOTER,
    HELP_LIMITED_SECTION,
    HELP_ADMIN_SECTION,

    // Applies to >premium commands
    PREMIUM_EMBED_TITLE,
    PREMIUM_EMBED_FOOTER,
    PREMIUM_EMBED_DESCRIPTION_PURCHASED,
    PREMIUM_EMBED_DESCRIPTION_NOT_PURCHASED,

    // Applies to >preset commands
    PRESET_EMBED_TITLE,
    PRESET_EMBED_CONTENT,
    // list sub
    PRESET_LIST_EMBED_TITLE,
    PRESET_LIST_DEFAULT_ENTRY,
    // create sub
    PRESET_CREATE_RESTRICTED_NAME,
    PRESET_CREATE_LIMIT_REACHED,
    PRESET_CREATE_LIMIT_REACHED_PREMIUM,
    PRESET_CREATE_ALREADY_CALLED,
    PRESET_CREATE_NAME_TOO_LONG,
    PRESET_CREATED,
    // export/import sub
    PRESET_EXPORTED_ALL,
    PRESET_EXPORTED_SINGLE,
    PRESET_IMPORT_NO_ATTACHMENT,
    PRESET_IMPORT_TOO_MANY,
    PRESET_IMPORT_INVALID_FILE,
    PRESET_IMPORT_CONFIRM_OVERRIDE_PLURAL,
    PRESET_IMPORT_CONFIRM_OVERRIDE_SINGULAR,
    PRESET_IMPORT_CONFIRM,
    PRESET_IMPORTED_PLURAL,
    PRESET_IMPORTED_SINGULAR,
    // delete sub
    PRESET_DELETE_SHOW_PRESETS_ADDON,
    PRESET_DELETE_IN_USE,
    PRESET_DELETED,
    // options sub
    PRESET_OPTIONS_LIST_OPTIONS_EMBED_TITLE,
    // list options sub
    PRESET_HAS_NO_SETTINGS,
    PRESET_OPTIONS_LIST_EMBED_TITLE,
    // set option sub
    PRESET_CANNOT_MODIFY_DEFAULT,
    PRESET_SETTING_INCORRECT_INPUT,
    PRESET_SETTING_SET,
    // preset settings descriptions
    PRESET_ENABLE_REACT_TO_ENTER_DESCRIPTION,
    PRESET_REACT_TO_ENTER_EMOJI_DESCRIPTION,
    PRESET_ENABLE_MESSAGE_ENTRIES_DESCRIPTION,
    PRESET_ENTRIES_PER_MESSAGE_DESCRIPTION,
    PRESET_MAX_ENTRIES_DESCRIPTION,
    PRESET_PING_WINNERS_DESCRIPTION,
    PRESET_ENABLE_WINNERS_MESSAGE,
    PRESET_ENABLE_DM_WINNERS,
    // preset settings limits
    PRESET_ENTRIES_PER_MESSAGE_LIMIT_MESSAGE,
    PRESET_MAX_ENTRIES_LIMIT_MESSAGE,
    ;

    public static Text match(String identifier) {
        return Text.valueOf(identifier.toUpperCase().replace("-", "_"));
    }
}
