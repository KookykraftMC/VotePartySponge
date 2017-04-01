package com.kookykraftmc.voteparty;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

/**
 * Created by TimeTheCat on 4/1/2017.
 */
public class VPStartPartyCommand implements CommandExecutor {
    VoteParty pl = VoteParty.getInstance();
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        pl.party();
        return CommandResult.success();
    }
}
