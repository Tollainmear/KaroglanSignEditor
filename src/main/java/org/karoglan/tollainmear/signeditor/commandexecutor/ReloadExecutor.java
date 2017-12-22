package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KSECommandManager;
import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.Translator;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;

public class ReloadExecutor implements CommandExecutor {
    private KSERecordsManager rm;
    private KaroglanSignEditor kse;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        kse = KaroglanSignEditor.getInstance();
        rm = KSERecordsManager.getInstance();
        try {
            kse.cfgInit();
            rm.init(kse);
            kse.setTranslator(new Translator(kse));
            kse.getTranslator().checkUpdate();
            kse.setKseCmdManager(new KSECommandManager(kse));
        } catch (IOException e) {
            e.printStackTrace();
        }
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(kse.getTranslator().getstring("message.KSEprefix")+kse.getTranslator().getstring("message.reload")));
        return CommandResult.success();
    }
}
