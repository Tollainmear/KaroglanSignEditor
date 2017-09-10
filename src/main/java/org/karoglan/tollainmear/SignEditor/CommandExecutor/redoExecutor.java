package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KSERecordsManager;
import org.karoglan.tollainmear.SignEditor.utils.KSEStack;
import org.karoglan.tollainmear.SignEditor.utils.Translator;
import org.karoglan.tollainmear.SignEditor.utils.mainController;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.util.Optional;

public class redoExecutor implements CommandExecutor {
    private mainController mc = new mainController();
    private KSEStack kseStack;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }

        Optional<Player> playerOpt;
        if (!(playerOpt = mc.getPlayerOpt(src)).isPresent() || playerOpt.equals(null)) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }
        Player player = playerOpt.get();

        if (!mc.getSign(player).isPresent()) {

            return CommandResult.empty();
        }
        TileEntity sign = mc.getSign(player).get();

        if (mc.hasKseStack(sign)) {
            kseStack = KSERecordsManager.getOperationStack().get(sign.getLocation());
        } else {
            mc.notice(player, Translator.getText("message.stackRedoEmpty"));
            CommandResult.empty();
        }

        if (kseStack.getNow() == kseStack.getTail()) {
            mc.notice(player, Translator.getText("message.stackRedoEmpty"));
            CommandResult.empty();
        } else {
            kseStack.setNow(kseStack.getNow() + 1 > 9 ? 0 : kseStack.getNow() + 1);
            Text[] textArray = kseStack.getTextStack(kseStack.getNow());
            for (int i = 0; i < 4; i++) {
                mc.setText(sign, i + 1, TextSerializers.FORMATTING_CODE.serialize(textArray[i] == null ? Text.of("") : textArray[i]));
            }
            mc.notice(player, Translator.getText("message.redoDone"));
            KSERecordsManager.getOperationStack().put(sign.getLocation(), kseStack);
            try {
                kseStack.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return CommandResult.success();
    }
}
