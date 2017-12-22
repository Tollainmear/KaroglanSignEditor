package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class VersionExecutor implements CommandExecutor {
    private MainController mc = new MainController();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        mc.noticeVersion(src);
        return CommandResult.success();
    }
}
