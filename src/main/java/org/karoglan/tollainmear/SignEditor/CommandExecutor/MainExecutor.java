package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
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

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> contents = new ArrayList<>();

        contents.add(Text.of(TextColors.GOLD, "/kse set [Lines] [Text]", TextColors.GRAY, " - ", TextColors.YELLOW, "Edit the target sign with [Text] at [Line]"));
        contents.add(Text.of(TextColors.GOLD, "/kse clear <Line>", TextColors.GRAY, " - ", TextColors.YELLOW, "Clear the Lines.The sign will be cleared if Lines was't define."));
        contents.add(Text.of(TextColors.GOLD, "/kse copy", TextColors.GRAY, " - ", TextColors.YELLOW, "Copy the text from the sign which you looking at."));
        contents.add(Text.of(TextColors.GOLD, "/kse paste <Line>", TextColors.GRAY, " - ", TextColors.YELLOW, "Paste the text (from your clipboard) for the sign which you looking at."));
        contents.add(Text.of(TextColors.GOLD, "/kse clipboard", TextColors.GRAY, " - ", TextColors.YELLOW, "Check your clipboard."));
        contents.add(Text.of(TextColors.GOLD, "/kse version", TextColors.GRAY, " - ", TextColors.YELLOW, "Show the KSE's version."));
        contents.add(Text.of(TextColors.GOLD, "/kse copy", TextColors.GRAY, " - ", TextColors.YELLOW, "Copy the text from the sign which you looking at."));
        contents.add(Text.of("Submit issues at http://www.mcbbs.net/thread-726021-1-1.html"));

        PaginationList.builder()
                .title(Text.of(KaroglanSignEditor.getPluginName()))
                .contents(contents)
                .padding(Text.of(TextColors.GRAY, "-"))
                .sendTo(src);
        return CommandResult.success();

    }
}
