package com.kookykraftmc.voteparty;

import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

import static org.spongepowered.api.text.TextTemplate.arg;

/**
 * Created by TimeTheCat on 4/1/2017.
 */
public class Texts {

    public static TextTemplate broadcastMessage = TextTemplate.of(
            arg("votes").color(TextColors.WHITE),
            TextColors.LIGHT_PURPLE, " votes need until a VoteParty!"
    );

    public static TextTemplate partyMessage = TextTemplate.of(
            TextFormat.of(TextColors.LIGHT_PURPLE, TextStyles.BOLD), "VoteParty ",
            TextFormat.of(TextColors.GREEN, TextStyles.BOLD), "ACTIVATED!"
    );

    public static void setBroadcastMessage(TextTemplate broadcastMessage) {
        Texts.broadcastMessage = broadcastMessage;
    }

    public static TextTemplate getBroadcastMessage() {
        return broadcastMessage;
    }

    public static TextTemplate getPartyMessage() {
        return partyMessage;
    }

    public static void setPartyMessage(TextTemplate partyMessage) {
        Texts.partyMessage = partyMessage;
    }
}
