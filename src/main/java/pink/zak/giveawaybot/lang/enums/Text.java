package pink.zak.giveawaybot.lang.enums;

public enum Text {
    // Applies everywhere
    CANT_FIND_MEMBER,
    NO_ACTIVE_GIVEAWAYS, // Might be moved to entries
    FATAL_ERROR_LOADING_SERVER,

    // Applies to both ban commands
    CANNOT_BAN_SELF,
    NOT_ENOUGH_PERMISSIONS_BAN,

    // Applies to >ban
    BAN_HELP,
    TARGET_ALREADY_BANNED,
    CANNOT_BAN_IS_SHADOW_BANNED, // You cannot ban someone normally if they are shadow banned
    BANNED_SUCCESSFULLY,

    // Applies to >sban
    SHADOW_BAN_HELP,
    TARGET_ALREADY_SHADOW_BANNED,
    CANNOT_BAN_IS_BANNED, // You cannot shadow ban someone if they are banned normally.
    SHADOW_BANNED_SUCCESSFULLY,

    // Applies to >entries

    SELF_BANNED_FROM_GIVEAWAYS,
    TARGET_BANNED_FROM_GIVEAWAYS,
    SELF_NOT_ENTERED,
    TARGET_NOT_ENTERED,
    // Contentful
    ENTRIES_EMBED_TITLE,
    ENTRIES_EMBED_GIVEAWAY_LINE, // This is what is used for every line. One giveaway per line.
    ENTRIES_EMBED_GIVEAWAY_LINE_PLURAL,
    ;

    public static Text match(String identifier) {
        return Text.valueOf(identifier.toUpperCase().replace("-", "_"));
    }
}
