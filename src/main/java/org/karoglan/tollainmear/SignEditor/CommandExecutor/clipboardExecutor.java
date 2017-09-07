package org.karoglan.tollainmear.SignEditor.CommandExecutor;

import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.karoglan.tollainmear.SignEditor.utils.mainController;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

public class clipboardExecutor implements CommandExecutor {
    private mainController mc = new mainController();
    private KaroglanSignEditor plugin = new KaroglanSignEditor();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            mc.playerNotFound(src);
        }
        Optional<Player> playerOpt = ((Player) src).getPlayer();
        if (!(playerOpt.isPresent())) {
            mc.playerNotFound(src);
            return CommandResult.success();
        }
        Player player = playerOpt.get();
        Text[] textArray = KaroglanSignEditor.copylist.get(player.getName());

        for (int i = 0; i < 4; i++) {
            player.sendMessage(
                    TextSerializers.FORMATTING_CODE
                            .deserialize("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6]")
                            .concat(Text.of(TextStyles.RESET, TextColors.GREEN
                                    , "|[Line] : ", TextStyles.BOLD, TextColors.DARK_GREEN, i + 1
                                    , TextStyles.RESET, TextColors.GREEN, " | "))
                            .concat(textArray[i]));
        }

        return CommandResult.success();
    }
}
