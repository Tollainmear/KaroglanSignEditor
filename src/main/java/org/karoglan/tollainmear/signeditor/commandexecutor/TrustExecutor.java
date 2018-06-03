package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.KSEStack;
import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.*;

public class TrustExecutor implements CommandExecutor {
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();
    private MainController mc = KaroglanSignEditor.getInstance().getMainController();
    private Map<String, Set<String>> whiteList = KSERecordsManager.getWhiteList();
    private Set<String> subWhiteList;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //todo-is a player？are trusting yourself? does target player already in trustlist?
        Sponge.getScheduler().createTaskBuilder().execute(task -> {
            String target = ((User) args.getOne(Text.of("player")).get()).getName();
            // Dose src was a player
            if (mc.isPLayer(src)) {
                Player player = (Player) src;
                if (player.getName() == target) {
                    mc.cantTrustSelf(player);
                    return;
                }
                whiteList = KSERecordsManager.getWhiteList();
                //if there was no record exist
                if (!(whiteList.containsKey(player.getName()))) {
                    subWhiteList = new LinkedHashSet<>();
                    whiteList.put(player.getName(), subWhiteList);
                } else {
                    subWhiteList = whiteList.get(player.getName());
                }
                if (subWhiteList.add(target)) {
                    mc.trustSuccessful(player,target);
                }else mc.alreadyTrusted(player);
            } else {
                mc.playerNotFound(src);
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
