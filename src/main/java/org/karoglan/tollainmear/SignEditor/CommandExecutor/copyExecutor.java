package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.karoglan.tollainmear.SignEditor.utils.mainController;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

public class copyExecutor implements CommandExecutor {
    mainController mc = new mainController();
    private KaroglanSignEditor plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (KaroglanSignEditor.copylist.isEmpty()){
            src.sendMessage(Text.of("Map is empty~"));
        }
        plugin = KaroglanSignEditor.getInstance();
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }
        Optional<Player> playerOpt = ((Player) src).getPlayer();
        if (!playerOpt.isPresent()) {
            mc.playerNotFound(src);
            return CommandResult.empty();
        }
        Player player = playerOpt.get();
        Optional<TileEntity> signOpt = mc.getSign(player);
        if (signOpt == null || !signOpt.isPresent()) {
            mc.signNotFound(player);
            return CommandResult.empty();
        }
        TileEntity sign = signOpt.get();

        Integer line = 1;
        Text[] textArray = new Text[4];
        for (int i = 0;i<4;i++) {
            textArray[i] = mc.getTargetText(sign, line);
            player.sendMessage(Text.of("|[Line] : " + line + " | ")
                    .concat(textArray[i]));
            line++;
        }

        KaroglanSignEditor.copylist.put(player.getName(),textArray);
        if (KaroglanSignEditor.copylist.containsKey(player.getName())){
        }

        return CommandResult.success();
    }
}
