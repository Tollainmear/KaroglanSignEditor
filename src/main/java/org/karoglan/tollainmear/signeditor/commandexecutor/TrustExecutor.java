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
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.*;

public class TrustExecutor implements CommandExecutor {
    private MainController mc = new MainController();
    KSEStack kseStack;
    private Map<String, Set<String>> whiteList = KSERecordsManager.getWhiteList();
    private Set<String> subWhiteList;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //todo-是玩家吗？有牌子吗？牌子是你的吗？trust的玩家是你自己吗？trust列表有这个玩家吗？
        Sponge.getScheduler().createTaskBuilder().execute(task -> {
            //Dose args valid?
            if (!((args.getOne(Text.of("player"))).get() instanceof Player)) {
                return;
            }
            Player target = (Player) args.getOne(Text.of("player")).get();

            // Dose src was a player
            if (mc.isPLayer(src)) {
                Player player = (Player) src;
                if (player.getName() == target.getName()) {
                    mc.cantTrustSelf(player);
                    return;
                }
                Optional<TileEntity> sign = mc.getSign(player);
                if (sign != null && sign.isPresent()) {
                    if (mc.hasKseStack(sign.get())) {
                        kseStack = KSERecordsManager.getOperationStack().get(mc.getSign(player));
                        //Dose Src was sign owner?
                        if (kseStack.isOwner(player)) {
                            whiteList = KSERecordsManager.getWhiteList();
                            //if there was no record exist
                            if (!(whiteList.containsKey(player.getName()))) {
                                subWhiteList = new LinkedHashSet<>();
                                whiteList.put(player.getName(), subWhiteList);
                            } else {
                                subWhiteList = whiteList.get(player.getName());
                            }
                            if (!(subWhiteList.add(target.getName()))) {
                                mc.alreadyTrusted(player);
                            }
                        } else {
                            mc.notOwner(player);
                            return;
                        }
                    }else {
                        mc.
                    }
                }
            } else mc.playerNotFound(src);
        }).submit(KaroglanSignEditor.getInstance());
        try
        {
            KSERecordsManager.getInstance().saveTrustList();
        } catch (
                IOException e)
        {
            e.printStackTrace();
        }
        return CommandResult.success();
    }
}
