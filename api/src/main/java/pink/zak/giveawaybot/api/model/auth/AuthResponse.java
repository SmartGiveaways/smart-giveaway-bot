package pink.zak.giveawaybot.api.model.auth;

public class AuthResponse {
    private boolean successful;
    private String token;

    public boolean isSuccessful() {
        return this.successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
