package org.karoglan.tollainmear.SignEditor.utils;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.karoglan.tollainmear.SignEditor.KSERecordsManager;
import org.karoglan.tollainmear.SignEditor.KaroglanSignEditor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.IOException;

public class ClipBoardContents {
    private KSERecordsManager recordsManager;
    private Text[] textArray = new Text[4];
    private static ClipBoardContents instance;

    public static ClipBoardContents getInstance() {
        return instance;
    }

    public void put(Player player, Text[] rawArray) throws IOException {
        recordsManager = KaroglanSignEditor.getKseRecordsManager();
        for (int i = 0; i < 4; i++) {
            textArray[i] = rawArray[i];
        }
        KSERecordsManager.getCopylist().put(player.getName(), this);
        recordsManager.save();
    }

    public Text[] get() {
        return textArray;
    }
}
