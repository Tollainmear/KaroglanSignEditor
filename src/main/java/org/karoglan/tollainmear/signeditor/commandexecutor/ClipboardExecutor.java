package org.karoglan.tollainmear.signeditor.commandexecutor;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.karoglan.tollainmear.signeditor.KaroglanSignEditor;
import org.karoglan.tollainmear.signeditor.utils.MainController;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class ClipboardExecutor implements CommandExecutor {
    private MainController mc = new MainController();
    private KaroglanSignEditor kse;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            kse = KaroglanSignEditor.getInstance();
            if (!(src instanceof Player)) {
                mc.playerNotFound(src);
            }
            Optional<Player> playerOpt = ((Player) src).getPlayer();
            if (!(playerOpt.isPresent())) {
                mc.playerNotFound(src);
                return;
            }
            Player player = playerOpt.get();
            if (!KSERecordsManager.getCopylist().containsKey(player.getName())) {
                kse.getLogger().info(player.getName() + " 38");
                for (String str : KSERecordsManager.getCopylist().keySet()) {
                    kse.getLogger().info(str + "40");
                }
                mc.nothingToPaste(src);
                return;
            }
            Text[] textArray = KSERecordsManager.getCopylist().get(player.getName()).get();

            mc.notice(player, kse.getTranslator().getText("message.onCopyText"));
            for (int i = 0; i < 4; i++) {
                mc.notice(player, i + 1, textArray[i]);
            }
        }).submit(KaroglanSignEditor.getInstance());
        return CommandResult.success();
    }
}
