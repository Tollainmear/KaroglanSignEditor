package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KSERecordsManager;
import org.karoglan.tollainmear.SignEditor.utils.KSEStack;
import org.karoglan.tollainmear.SignEditor.utils.mainController;
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

public class clearExecutor implements CommandExecutor {
    private KSEStack kseStack;
    private mainController mc = new mainController();
    private Integer line;
    private Text signText;
    private Optional<Player> playerOpt;
    private Player player;
    private Optional<TileEntity> signOpt;
    private TileEntity sign;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return CommandResult.success();
        }
        Optional<Integer> lineOpt = args.<Integer>getOne(Text.of("line"));
        if (!(playerOpt = mc.getPlayerOpt(src)).isPresent() || playerOpt == null) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }

        player = playerOpt.get();

        signOpt = mc.getSign(player);
        if (signOpt == null || !signOpt.isPresent()) {
            mc.signNotFound(player);
            return CommandResult.empty();
        }

        sign = signOpt.get();

        kseStack = mc.getKseStack(sign);

        try {
            kseStack.update(mc.getTextArray(sign), sign.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lineOpt.isPresent()) {
            line = lineOpt.get();
            if (!mc.isLinesValid(line)) {
                mc.linesWrong(src);
                return CommandResult.empty();
            }

            clearText(line);

            try {
                kseStack.add(mc.getTextArray(sign), sign.getLocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return CommandResult.success();
        } else {
            for (int i = 1; i < 5; i++) {
                clearText(i);
            }
            try {
                kseStack.add(mc.getTextArray(sign), sign.getLocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return CommandResult.success();

        }
    }

    private void clearText(int lines) {
        signText = mc.getTargetText(sign, lines);
        mc.setText(sign, lines, "");
        mc.notice(player, lines, signText, mc.getTargetText(sign, lines));
    }
}
