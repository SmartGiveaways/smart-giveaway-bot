package pink.zak.giveawaybot.discord.controllers;

public enum ReturnCode {

    SUCCESS,
    GENERIC_FAILURE,
    RATE_LIMIT_FAILURE,
    PERMISSIONS_FAILURE,
    NO_PRESET,
    GIVEAWAY_LIMIT_FAILURE,
    FUTURE_GIVEAWAY_LIMIT_FAILURE,
    UNKNOWN_EMOJI
}
