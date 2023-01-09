package net.devtm.tmtokens.API;

import net.devtm.tmtokens.service.ServiceHandler;

import java.util.UUID;

public class TokensPlayer {

    UUID uuid;
    int tokens;

    public TokensPlayer() {
        this.tokens = 0;
    }

    public TokensPlayer(UUID uuid, int tokens) {
        this.uuid = uuid;
        this.tokens = tokens;
    }

    public UUID getPlayer() {
        return uuid;
    }

    public void setPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public int getTokens() {
        return tokens;
    }

    /**
     * If you have the tokensPlayer you can set his
     * tokens balance to whatever you want
     * @param tokens A <bold>double</bold> number.
     */
    public void setTokens(int tokens) {
        this.tokens = tokens;
        if(uuid == null) return;
        ServiceHandler.SERVICE.getDataService().setTokens(uuid, this.tokens);
    }
    /**
     * If you have the tokensPlayer you can give a
     * player how many tokens you want
     * @param tokens A <bold>double</bold> number.
     */
    public void giveTokens(int tokens) {
        this.tokens = this.tokens + tokens;
        if(uuid == null) return;
        ServiceHandler.SERVICE.getDataService().setTokens(uuid, this.tokens);
    }
    /**
     * If you have the tokensPlayer you can remove
     * tokens from the player!
     * @param tokens A <bold>double</bold> number.
     */
    public void removeTokens(int tokens) {
        this.tokens = this.tokens - tokens;
        if(uuid == null) return;
        ServiceHandler.SERVICE.getDataService().setTokens(uuid, this.tokens);
    }
}
