package pink.zak.giveawaybot.lang.enums;

public enum Language {

    ENGLISH_UK("English (UK)", "english-uk", "english", "uk", "en-uk");

    private final String name;
    private final String[] identifiers;

    Language(String name, String... identifiers) {
        this.name = name;
        this.identifiers = identifiers;
    }

    public static Language match(String identifier) {
        String lowerIdentifier = identifier.toLowerCase();
        for (Language language : Language.values()) {
            for (String possibility : language.getIdentifiers()) {
                if (possibility.equals(lowerIdentifier)) {
                    return language;
                }
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String[] getIdentifiers() {
        return this.identifiers;
    }
}
