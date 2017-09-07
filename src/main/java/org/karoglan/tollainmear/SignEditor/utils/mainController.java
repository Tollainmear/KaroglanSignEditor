package org.karoglan.tollainmear.SignEditor.utils;

import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class mainController {

    public Optional<Player> isPlayer(CommandSource src) {
        Optional<Player> playerOpt = ((Player) src).getPlayer();
        if (playerOpt.isPresent())
            return playerOpt;
        return null;
    }

    public Optional<TileEntity> getSign(Player src) {
        Optional<Player> playerOpt = ((Player) src).getPlayer();
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            BlockRay<World> blockRay = BlockRay.from(player).distanceLimit(6).build();

            int count = 0;
            while (blockRay.hasNext()) {
                BlockRayHit<World> blockRayHit = blockRay.next();
                Location<World> Location = blockRayHit.getLocation();
                BlockType targetBlock = Location.getBlockType();
                if (targetBlock.equals(BlockTypes.AIR)) {
                    count++;
//                    src.sendMessage(Text.of(TextColors.GRAY, "Found" + targetBlock + "at" + count + "blocks away"));
                    continue;
                } else if (targetBlock.equals(BlockTypes.STANDING_SIGN) || targetBlock.equals(BlockTypes.WALL_SIGN)) {
                    count++;
//                    src.sendMessage(Text.of(TextColors.GREEN, "Found" + targetBlock + "at" + count + "blocks away"));
                    Optional<TileEntity> signOpt = Location.getTileEntity();
                    return signOpt;
                }
            }
        }
        return null;
    }

    public void setText(TileEntity sign, int lines, String str) {
        if (sign.supports(SignData.class)) {
            Text maintext = TextSerializers.FORMATTING_CODE.deserialize(str);
            SignData newdata = sign.getOrCreate(SignData.class).get();
            newdata.setElement(lines - 1, Text.of(maintext));
            sign.offer(newdata);
        }
    }

    public Text getTargetText(TileEntity Sign, int lines) {
        Optional<SignData> data = Sign.getOrCreate(SignData.class);
        if (data.isPresent()) {
            Text text = data.get().lines().get(lines - 1);
            return text == null ? Text.of("") : text;
        }
        return null;
    }

    public boolean isLinesValid(Integer lines) {
        return lines < 5 && lines > 0;
    }

    public void notice(Player player, Integer lines, String mode, Text oldText, Text newText) {
        player.sendMessage(TextSerializers.FORMATTING_CODE
                .deserialize
                        ("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6] Done ! The sign's text has been " + mode + ".")
                .concat(Text.of("\n"))
                .concat(Text.of(TextColors.GREEN, "[Line: ", TextStyles.BOLD, TextColors.DARK_GREEN, lines, TextStyles.RESET, TextColors.GREEN, "] "))
                .concat(oldText)
                .concat(TextSerializers.FORMATTING_CODE.deserialize(" &c&l>>&r "))
                .concat(newText));
    }

    public void notice(Player player, Integer line1, Integer line2, String mode, Text oldText, Text newText) {
        player.sendMessage(TextSerializers.FORMATTING_CODE
                .deserialize
                        ("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6] Done ! The sign's text has been " + mode + ".")
                .concat(Text.of("\n"))
                .concat(Text.of(TextColors.GREEN, "[Line: ", TextStyles.BOLD, TextColors.DARK_GREEN, line1, TextColors.AQUA, "<=>", TextColors.DARK_GREEN, line2, TextStyles.RESET, TextColors.GREEN, "] "))
                .concat(oldText)
                .concat(TextSerializers.FORMATTING_CODE.deserialize(" &c&l<=>&r "))
                .concat(newText));
    }

    public void signNotFound(Player player) {
        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize
                ("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6]" + "&cCould not found the sign."));
    }

    public void playerNotFound(CommandSource src) {
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize
                ("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6]" + "&cCould not found the player."));
    }

    public void linesWrong(CommandSource src) {
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize
                ("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6]" + "&cYou must assign a line number between 1-4."));
    }

    public void nothingToPaste(CommandSource src) {
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize
                ("&6[&e&l" + KaroglanSignEditor.getPluginName() + "&r&6]" + "&cYou have nothing to paste,please use copy command first."));
    }
}

