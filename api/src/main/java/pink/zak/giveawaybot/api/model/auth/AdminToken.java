package pink.zak.giveawaybot.api.model.auth;

import pink.zak.giveawaybot.api.service.TokenGenerator;

public class AdminToken {
    private final String token;
    private final long issueTime;

    public AdminToken(String token, long issueTime) {
        this.token = token;
        this.issueTime = issueTime;
    }

    public AdminToken() {
        this.token = TokenGenerator.generate();
        this.issueTime = System.currentTimeMillis();
    }

    public String getToken() {
        return this.token;
    }

    public long getIssueTime() {
        return this.issueTime;
    }
}
