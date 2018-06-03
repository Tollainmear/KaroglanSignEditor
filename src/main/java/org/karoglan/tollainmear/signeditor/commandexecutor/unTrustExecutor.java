package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class unTrustExecutor implements CommandExecutor {
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();
    private MainController mc = KaroglanSignEditor.getInstance().getMainController();
    private Map whiteList;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().execute(()->{
            if (!(src instanceof Player)){
                mc.playerNotFound(src);
                return;
            }
            Player player = mc.getPlayerOpt(src).get();
            whiteList = KSERecordsManager.getWhiteList();
            if (whiteList.containsKey(player.getName())){
                Set subWhiteList = (Set) whiteList.get(player.getName());
                String targetPlayer = ((User)args.getOne(Text.of("player")).get()).getName();
                if (subWhiteList.remove(targetPlayer)){
                    mc.untrustSuccessful(player,targetPlayer);
                }else mc.untrustFailed(player,targetPlayer);
            }
            try {
                kse.getKSERecordsManager().saveTrustList();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }).submit(KaroglanSignEditor.getInstance());
        return CommandResult.success();
    }
}
