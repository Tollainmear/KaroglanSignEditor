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
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class KSEListener {
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();
    private Translator translator = kse.getTranslator();

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

    @Listener
    public void onBreakSignEvent(ChangeBlockEvent.Break e, @First Player player) {
        List<Transaction<BlockSnapshot>> transactionList = e.getTransactions();
        //dose target block was a sign?
        for (Transaction<BlockSnapshot> trans : transactionList) {
            if (isSign(trans.getOriginal())) {
                Location<World> loc = trans.getOriginal().getLocation().get();
                TileEntity sign = loc.getTileEntity().get();
                if (!KSERecordsManager.getOperationStack().containsKey(loc.toString())) {
                    player.sendMessage(translator.getText("message.KSEprefix")
                            .concat(translator.getText("message.unownedSign"))
                            .toBuilder()
                    .onClick(TextActions.runCommand("/kse set 1"+ sign.get(Keys.SIGN_LINES).get().get(0).toPlain()))
                    .onClick(TextActions.runCommand("/kse set 2"+ sign.get(Keys.SIGN_LINES).get().get(1).toPlain()))
                    .onClick(TextActions.runCommand("/kse set 3"+ sign.get(Keys.SIGN_LINES).get().get(2).toPlain()))
                    .onClick(TextActions.runCommand("/kse set 4"+ sign.get(Keys.SIGN_LINES).get().get(3).toPlain()))
                            .onClick(TextActions.executeCallback(callback ->{ player.sendMessage(translator.getText("message.onChangeText"));}))
                    .onHover(TextActions.showText(translator.getText("message.updateAfterReplace")))
                    .build());
                    return;
                } else {
                    KSEStack kseStack = KSERecordsManager.getOperationStack().get(loc.toString());
                }
                //dose the causer player has permission to edit it?
                if ()
                if (wasSignCollaborator(player.getName(), loc.toString()) || hasBypassPermission(player)) {
                    //remove operation history then save it.
                    KSERecordsManager.getOperationStack().remove(loc.toString());
                    try {
                        KaroglanSignEditor.getInstance().getKSERecordsManager().saveOperationHistory();
                    } catch (IOException e1) {
                        translator.logWarn("error.saveFailed");
                        e1.printStackTrace();
                        player.sendMessage(translator.getText("error.saveFailed"));
                    }
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
        //is the block was a sign?
        if (isSign(e.getTargetBlock())) {
            Location<World> loc = e.getTargetBlock().getLocation().get();
            TileEntity sign = loc.getTileEntity().get();
            //has operation history?
            if (KSERecordsManager.getOperationStack().containsKey(loc.toString())) {
                KSEStack kseStack = KSERecordsManager.getOperationStack().get(loc.toString());
                //is the sign owner or was in whitelist or has bypass permission
                if (kseStack.isOwner(player.getName()) || wasSignCollaborator(player.getName(), loc.toString()) || hasBypassPermission(player)) {
                    suggestCMC(player, sign);
                }
            }
            //might created by some plugin or old version kse
            else {
                if (hasBypassPermission(player)) {
                    //suggestion command
                    suggestCMC(player, sign);
                }
            }
        }
    }

    private void suggestCMC(Player player, TileEntity sign) {
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.KSEdescription")));
        if (isEmpty(sign)) {
            return;
        } else {
            for (int i = 0; i < 4; i++) {
                int line = i + 1;
                player.sendMessage(Text.of()
                        .concat(Text.of(TextColors.GREEN,"Lines : "+line+" | "))
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
        for (int i = 0; i<4;i++){
            if (sign.get(Keys.SIGN_LINES).get().get(i).isEmpty()){
                continue;
            }return false;
        }return true;
    }

    private void signInfo(Player player, String loc) {
        player.sendMessage(translator.getText("message.KSEprefix")
                .concat(translator.getText("message.owner"))
                .concat(translator.deserialize("&7 : &6&l" + KSERecordsManager.getOperationStack().get(loc).getOwner())));
    }

    private boolean wasSignCollaborator(String player, String loc) {
        //has this sign history?
        if (KSERecordsManager.getOperationStack().containsKey(loc)) {
            KSEStack kseStack = KSERecordsManager.getOperationStack().get(loc);
            //is the owner of this sign?
            if (kseStack.isOwner(player)) {
                return true;
            }
            //is the collaborator of this sign?
            else {
                //does this whitelist was recorded before?
                if (KSERecordsManager.getWhiteList().containsKey(kseStack.getOwner())) {
                    Set<String> whitelist = KSERecordsManager.getWhiteList().get(kseStack.getOwner());
                    return whitelist.contains(player);
                }
            }
        }
        return false;
    }

    private boolean hasBypassPermission(Player player) {
        return player.hasPermission("kse.bypass");
    }

    private boolean isSign(BlockSnapshot targetBlock) {
        return (targetBlock.getState().getType() == BlockTypes.STANDING_SIGN || targetBlock.getState().getType() == BlockTypes.WALL_SIGN);
    }
}
