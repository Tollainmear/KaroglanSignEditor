package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.Translator;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class MainExecutor implements CommandExecutor {
    Translator translator;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> contents = new ArrayList<>();
        translator = KaroglanSignEditor.getInstance().getTranslator();

        contents.clear();

        contents.add(Text.of(TextColors.GOLD, "/kse set [Lines] [Text]", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.edit")));
        contents.add(Text.of(TextColors.GOLD, "/kse clear <Line>", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.clear")));
        contents.add(Text.of(TextColors.GOLD, "/kse trust [Player]", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.trust")));
        contents.add(Text.of(TextColors.GOLD, "/kse untrust [Player]", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.untrust")));
        contents.add(Text.of(TextColors.GOLD, "/kse trustlist", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.trustList")));        contents.add(Text.of(TextColors.GOLD, "/kse copy", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.copy")));
        contents.add(Text.of(TextColors.GOLD, "/kse paste <Line>", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.paste")));
        contents.add(Text.of(TextColors.GOLD, "/kse clipboard", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.clipboard")));
        contents.add(Text.of(TextColors.GOLD, "/kse undo", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.undo")));
        contents.add(Text.of(TextColors.GOLD, "/kse redo", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.redo")));
        contents.add(Text.of(TextColors.GOLD, "/kse on/off", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.onOrOff")));
        contents.add(Text.of(TextColors.GOLD, "/kse version", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.version")));
        contents.add(Text.of(TextColors.GOLD, "/kse reload", TextColors.GRAY, " - ", TextColors.YELLOW, translator.getstring("command.reload")));
        contents.add(Text.of(translator.getstring("github")));

        PaginationList.builder()
                .title(Text.of(KaroglanSignEditor.getPluginName()))
                .contents(contents)
                .padding(Text.of(TextColors.GRAY, "-"))
                .sendTo(src);
        return CommandResult.success();

    }
}
