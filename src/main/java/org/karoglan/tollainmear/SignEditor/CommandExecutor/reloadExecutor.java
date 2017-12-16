package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import org.karoglan.tollainmear.SignEditor.KSERecordsManager;
import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.karoglan.tollainmear.SignEditor.utils.Translator;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;

public class reloadExecutor implements CommandExecutor {
    private KSERecordsManager rm;
    private KaroglanSignEditor kse;
    private CommentedConfigurationNode newNode;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        kse = KaroglanSignEditor.getInstance();
        rm = KSERecordsManager.getInstance();
        try {
            kse.cfgInit();
            rm.init(kse);
            kse.setTranslator(new Translator(kse));
        } catch (IOException e) {
            e.printStackTrace();
        }
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(kse.getTranslator().getstring("message.KSEprefix")+kse.getTranslator().getstring("message.reload")));
        return CommandResult.success();
    }
}
