package org.karoglan.tollainmear.signeditor.utils;

import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.List;

public class KSEListener {
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();

    @Listener
    public void onPlaceSignEvent (ChangeBlockEvent.Place e, @First Player player){
        List<Transaction<BlockSnapshot>> transaction = e.getTransactions();
        for (Transaction<BlockSnapshot> transaction1:transaction){
            if(isSign(transaction1)) {
                //todo - create a KSEstack
                KSEStack kseStack = new KSEStack(player.getName());
                //todo - Create a Whitelist
            }
        }
    }

    @Listener
    public void onBreakSignEvent (ChangeBlockEvent.Break e){
        List<Transaction<BlockSnapshot>> transaction = e.getTransactions();
        for (Transaction<BlockSnapshot> transaction1:transaction){
            if(isSign(transaction1)) {
                //todo - remove operation history
                //todo - remove Whitelist
            }
        }
    }

    private boolean isSign(Transaction<BlockSnapshot> transaction){
        return (transaction.getOriginal().getState().getType() == BlockTypes.STANDING_SIGN || transaction.getOriginal().getState().getType() == BlockTypes.WALL_SIGN);
    }
}
