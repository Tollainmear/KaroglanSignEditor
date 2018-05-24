package org.karoglan.tollainmear.signeditor.commandexecutor;

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
    private MainController mc = new MainController();
    private Integer line;
    private Text signText;
    private Optional<Player> playerOpt;
    private Player player;
    private Optional<TileEntity> signOpt;
    private TileEntity sign;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //todo-是玩家吗？有牌子吗？是所有者吗？在白名单里吗？
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (!(src instanceof Player)) {
                mc.playerNotFound(src);
                return;
            }
            Optional<Integer> lineOpt = args.<Integer>getOne(Text.of("line"));

            if (!(playerOpt = mc.getPlayerOpt(src)).isPresent() || playerOpt == null) {
                mc.playerNotFound(src);
                return;
            }

            player = playerOpt.get();

            signOpt = mc.getSign(player);
            if (signOpt == null || !signOpt.isPresent()) {
                mc.signNotFound(player);
                return;
            }

            sign = signOpt.get();

            kseStack = mc.getKseStack(sign,player);

            try {
                kseStack.update(mc.getTextArray(sign), sign.getLocation());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (lineOpt.isPresent()) {
                line = lineOpt.get();
                if (!mc.isLinesValid(line)) {
                    mc.linesWrong(src);
                    return;
                }

                clearText(line);

                try {
                    kseStack.add(mc.getTextArray(sign), sign.getLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } else {
                for (int i = 1; i < 5; i++) {
                    clearText(i);
                }
                try {
                    kseStack.add(mc.getTextArray(sign), sign.getLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;

            }
        }).submit(KaroglanSignEditor.getInstance());
        return CommandResult.success();
    }

    private void clearText(int lines) {
        signText = mc.getTargetText(sign, lines);
        mc.setText(sign, lines, "");
        mc.notice(player, lines, signText, mc.getTargetText(sign, lines));
    }
}
