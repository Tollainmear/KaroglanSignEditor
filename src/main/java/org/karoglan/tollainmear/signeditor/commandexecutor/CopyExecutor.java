package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.ClipBoardContents;
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

public class CopyExecutor implements CommandExecutor {
    private KaroglanSignEditor kse = KaroglanSignEditor.getInstance();
    private MainController mc = KaroglanSignEditor.getInstance().getMainController();
    private ClipBoardContents cbc = KaroglanSignEditor.getClipBoardContents();;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return;
        }
        Optional<Player> playerOpt = ((Player) src).getPlayer();
        if (!playerOpt.isPresent()) {
            mc.playerNotFound(src);
            return;
        }
        Player player = playerOpt.get();
        Optional<TileEntity> signOpt = mc.getSign(player);
        if (signOpt == null || !signOpt.isPresent()) {
            mc.signNotFound(player);
            return;
        }
        TileEntity sign = signOpt.get();

        mc.notice(player, kse.getTranslator().getText("message.onCopyText"));
        Integer line = 1;
        Text[] textArray = new Text[4];
        for (int i = 0; i < 4; i++) {
            textArray[i] = mc.getTargetText(sign, line);
            mc.notice(player, i + 1, textArray[i]);
            line++;
        }

        try {
            if (player == null) {
                KaroglanSignEditor.getInstance().getLogger().warn("Player is null");
                return;
            }
            if (textArray == null) {
                KaroglanSignEditor.getInstance().getLogger().warn("text is null");
                return;
            }
            cbc.put(player, textArray);
        } catch (IOException e) {
            e.printStackTrace();
        }}).submit(KaroglanSignEditor.getInstance());

        return CommandResult.success();
    }
}
