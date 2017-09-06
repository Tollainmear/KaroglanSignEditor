package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.utils.mainController;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class clearExecutor implements CommandExecutor {
    private mainController mc = new mainController();
    private String mode = "cleared";
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
        if ((playerOpt = mc.isPlayer(src)).isPresent()) {
            player = playerOpt.get();
            signOpt = mc.getSign(player);
            if (lineOpt.isPresent()) {
                line = lineOpt.get();
                if (!mc.isLinesValid(line)) {
                    mc.linesWrong(src);
                    return CommandResult.empty();
                }
                if (signOpt.isPresent()) {
                    clearText(line);
                    return CommandResult.success();
                }
            }
            if (signOpt != null && signOpt.isPresent()) {
                for (int i = 1; i < 5; i++) {
                    clearText(i);
                }
                return CommandResult.success();
            }
            mc.signNotFound(player);
            return CommandResult.empty();
        }
        mc.playerNotFound(src);
        return CommandResult.empty();
    }

    private void clearText(int lines) {
        sign = signOpt.get();
        signText = mc.getTargetText(sign, lines);
        mc.setText(sign, lines, "");
        mc.notice(player, lines, mode, signText, mc.getTargetText(sign, lines));
    }
}
