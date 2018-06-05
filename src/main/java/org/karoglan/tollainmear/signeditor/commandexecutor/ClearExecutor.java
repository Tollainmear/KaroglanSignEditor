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
import java.util.Optional;

public class ClearExecutor implements CommandExecutor {
    private KSEStack kseStack;
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();
    private MainController mc = KaroglanSignEditor.getInstance().getMainController();
    //private MainController mc = new MainController();
    private TileEntity sign;
    private Player player;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //todo-is player?has sign?has bypass permission?was the owner? was trusted?
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            //is a player?
            if (!(src instanceof Player)) {
                mc.playerNotFound(src);
                return;
            }

            Optional<Integer> lineOpt = args.<Integer>getOne(Text.of("line"));
            player = mc.getPlayerOpt(src).get();
            Optional<TileEntity> signOpt = mc.getSign(player);

            //if no sign was find
            if (signOpt == null || !(signOpt.isPresent())) {
                mc.signNotFound(player);
                return;
            }

            sign = signOpt.get();
            kseStack = mc.getKseStack(sign,player);

            //if player has bypass permission
            if (mc.couldModify(player,kseStack)){
                try {
                    kseStack.update(mc.getTextArray(sign), sign.getLocation());
                } catch (IOException e) {
                    KaroglanSignEditor.getInstance().getTranslator().logWarn("error.saveFailed");
                }

                if (lineOpt.isPresent()) {
                    Integer line = lineOpt.get();
                    if (!mc.isLinesValid(line)) {
                        mc.linesWrong(player);
                        return;
                    }

                    clearText(line);

                    try {
                        kseStack.add(mc.getTextArray(sign), sign.getLocation());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int i = 1; i < 5; i++) {
                        clearText(i);
                    }
                    try {
                        kseStack.add(mc.getTextArray(sign), sign.getLocation());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }else {
                mc.notPermitted(player,kseStack);
            }
        }).submit(KaroglanSignEditor.getInstance());
        return CommandResult.success();
    }

    private void clearText(int lines) {
        Text signText = mc.getTargetText(sign, lines);
        mc.setText(sign, lines, "");
        mc.notice(player, lines, signText, mc.getTargetText(sign, lines));
    }

}
