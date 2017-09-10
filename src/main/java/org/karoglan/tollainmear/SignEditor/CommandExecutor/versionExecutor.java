package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.karoglan.tollainmear.SignEditor.utils.mainController;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.serializer.TextSerializers;

public class versionExecutor implements CommandExecutor {
    private mainController mc = new mainController();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        mc.noticeVersion(src);
        return CommandResult.success();
    }
}
