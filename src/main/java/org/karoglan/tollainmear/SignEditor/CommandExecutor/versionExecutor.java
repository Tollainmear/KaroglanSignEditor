package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.serializer.TextSerializers;

public class versionExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(TextSerializers.FORMATTING_CODE
                .deserialize
                        ("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6]" + "&aVersion &7: &e" + KaroglanSignEditor.getVersion() + "&a."));
        return CommandResult.success();
    }
}
