package org.karoglan.tollainmear.signeditor.utils;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class KSEListener {
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();
    private Translator translator = kse.getTranslator();
    private MainController mc = kse.getMainController();

    @Listener
    public void onPlaceSignEvent(ChangeBlockEvent.Place e, @First Player player) {
        List<Transaction<BlockSnapshot>> transactionList = e.getTransactions();
        for (Transaction<BlockSnapshot> trans : transactionList) {
            if (isSign(trans.getFinal())) {
                KSEStack kseStack = new KSEStack(player.getName());
                KSERecordsManager.getOperationStack().put(trans.getOriginal().getLocation().get().toString(), kseStack);
                try {
                    KaroglanSignEditor.getInstance().getKSERecordsManager().saveOperationHistory();
                } catch (IOException e1) {
                    KaroglanSignEditor.getInstance().getTranslator().logWarn("error.saveFailed");
                }
            }
        }
    }

    @Listener(order = Order.PRE)
    public void onBreakSignEvent(ChangeBlockEvent.Break e, @First Player player) {
        List<Transaction<BlockSnapshot>> transactionList = e.getTransactions();
        //dose target block was a sign?
        for (Transaction<BlockSnapshot> trans : transactionList) {
            if (isSign(trans.getOriginal())) {
                Location<World> loc = trans.getOriginal().getLocation().get();
                KSEStack kseStack;
                //if the sign was no record founded
                if (!KSERecordsManager.getOperationStack().containsKey(loc.toString())) {
                    return;
                } else {
                    kseStack = KSERecordsManager.getOperationStack().get(loc.toString());
                }
                //dose the target player has permission to edit it?
                if (mc.couldModify(player, kseStack)) {
                    //remove operation history then save it.
                    KSERecordsManager.getOperationStack().remove(loc.toString());
                    try {
                        KaroglanSignEditor.getInstance().getKSERecordsManager().saveOperationHistory();
                    } catch (IOException e1) {
                        translator.logWarn("error.saveFailed");
                        e1.printStackTrace();
                        player.sendMessage(translator.getText("error.saveFailed"));
                    }
                    return;
                } else {
                    e.setCancelled(true);
                    player.sendMessage(translator.getText("message.KSEprefix")
                            .concat(translator.getText("message.noPermission")));
                    signInfo(player, loc.toString());
                }
            }
        }
    }

    @Listener
    public void onPlayerInteractSign(InteractBlockEvent.Secondary e, @First Player player) {
        if (KaroglanSignEditor.playerState.containsKey(player.getName())){
            if (!KaroglanSignEditor.playerState.get(player.getName())){
                return;
            }
        }
        //is the block was a sign?
        if (isSign(e.getTargetBlock())) {
            Location<World> loc = e.getTargetBlock().getLocation().get();
            TileEntity sign = loc.getTileEntity().get();
            //has operation history?
            if (KSERecordsManager.getOperationStack().containsKey(loc.toString())) {
                KSEStack kseStack = KSERecordsManager.getOperationStack().get(loc.toString());
                //is the sign owner or was in whitelist or has bypass permission
                if (mc.couldModify(player, kseStack)) {
                    suggestCMC(player, sign);
                }
            }
            //might created by some plugin or old version kse
            else {
                if (player.hasPermission("kse.bypass")) {
                    //suggestion command
                    suggestCMC(player, sign);
                    player.sendMessage(translator.getText("message.KSEprefix")
                            .concat(translator.getText("message.ownerlessSign"))
                            .toBuilder()
                            .onHover(TextActions.showText(translator.getText("message.updateAfterReplace")))
                            .build());
                }
            }
        }
    }

    private void suggestCMC(Player player, TileEntity sign) {
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.KSEdescription"))
                .toBuilder()
                .onClick(TextActions.runCommand("/kse")).build());
        if (!isEmpty(sign)) {
            for (int i = 0; i < 4; i++) {
                int line = i + 1;
                player.sendMessage(Text.of()
                        .concat(Text.of(TextColors.GREEN, TextStyles.UNDERLINE, "Lines : " + line + " | "))
                        .concat(sign.get(Keys.SIGN_LINES).get().get(i))
                        .toBuilder()
                        .onClick(TextActions.suggestCommand("/kse set " + line + " " + sign.get(Keys.SIGN_LINES).get().get(i).toPlain()))
                        .onHover(TextActions.showText(kse.getTranslator().getText("message.clickMe")))
                        .build());
            }
            player.sendMessage(translator.getText("message.editSuggestion"));
        }
    }

    private boolean isEmpty(TileEntity sign) {
        for (int i = 0; i < 4; i++) {
            if (sign.get(Keys.SIGN_LINES).get().get(i).isEmpty()) {
                continue;
            }
            return false;
        }
        return true;
    }

    private void signInfo(Player player, String loc) {
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.owner"))
                .concat(translator.deserialize("&7 : &6&l" + KSERecordsManager.getOperationStack().get(loc).getOwner())));
    }

    private boolean isSign(BlockSnapshot targetBlock) {
        return (targetBlock.getState().getType() == BlockTypes.STANDING_SIGN || targetBlock.getState().getType() == BlockTypes.WALL_SIGN);
    }
}
