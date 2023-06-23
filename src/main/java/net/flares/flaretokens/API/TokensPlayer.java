package net.flares.flaretokens.API;

import net.flares.flaretokens.service.ServiceHandler;

import java.util.UUID;

public class TokensPlayer {

    UUID uuid;
    int tokens;
    public long updatedTime;

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
     * If you have the MobcoinsPlayer you can set his
     * mobcoins balance to whatever you want
     * @param tokens A <bold>double</bold> number.
     */
    public void setTokens(int tokens) {
        this.tokens = tokens;
        if(uuid == null) return;
        ServiceHandler.SERVICE.getDataService().setTokens(uuid, this.tokens);

    }

    public void objectSetTokens(int tokens) {
        this.tokens = tokens;
    }


    /**
     * If you have the MobcoinsPlayer you can give a
     * player how many tokens you want
     * @param tokens A <bold>double</bold> number.
     */
    public void giveTokens(int tokens) {
        this.tokens = this.tokens + tokens;
        if(uuid == null) return;
        ServiceHandler.SERVICE.getDataService().setTokens(uuid, this.tokens);
    }
    /**
     * If you have the MobcoinsPlayer you can remove
     * tokens from the player!
     * @param tokens A <bold>double</bold> number.
     */
    public void removeTokens(int tokens) {
        this.tokens = this.tokens - tokens;
        if(uuid == null) return;
        ServiceHandler.SERVICE.getDataService().setTokens(uuid, this.tokens);
    }
}
