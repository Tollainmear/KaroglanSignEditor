package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.karoglan.tollainmear.signeditor.utils.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrustListExecutor implements CommandExecutor {
    private MainController mc = KaroglanSignEditor.getInstance().getMainController();
    private Translator translator = KaroglanSignEditor.getInstance().getTranslator();
    private List<Text> contents = new ArrayList<>();
    private Map whitelist;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().execute(()->{
            if (!(src instanceof Player)){
                mc.playerNotFound(src);
                return;
            }
            Player player = mc.getPlayerOpt(src).get();
            whitelist = KSERecordsManager.getWhiteList();
            if (whitelist.containsKey(player.getName())){
                Set subWhiteList = (Set) whitelist.get(player.getName());
                contents.clear();
                for (Object collaborator : subWhiteList){
                    contents.add(Text.of(TextColors.GREEN,collaborator).toBuilder()
                            .onClick(TextActions.suggestCommand("/kse untrust "+ collaborator))
                            .onHover(TextActions.showText(translator.getText("message.clickToRemove")))
                            .build());
                }
            }
            PaginationList.builder()
                    .header(translator.getText("message.trustlist"))
                    .linesPerPage(10)
                    .contents(contents)
                    .sendTo(player);
        }).submit(KaroglanSignEditor.getInstance());
        return CommandResult.success();

    }
}
