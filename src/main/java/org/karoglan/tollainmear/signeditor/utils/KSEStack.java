package org.karoglan.tollainmear.signeditor.utils;

import org.karoglan.tollainmear.signeditor.KSERecordsManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.List;


public class KSEStack {
    private KSERecordsManager recordsManager;
    //textStack[][0-3]will storage the Sign's text.
    private Text[][] textStack;
    private Integer now;
    private Integer tail;
    private Integer head;
    private String owner;

    public KSEStack(String owner) {
        now = 0;
        head = 0;
        tail = 1;
        textStack = new Text[10][4];
        this.owner = owner;
    }

    public void set(Text[][] textStack) {
        this.textStack = textStack;
    }

    public void add(Text[] textArray, Location<World> loc) throws IOException {
        now++;
        if (now == 10) {
            now = 0;
        }
        tail = now;
        if (tail.equals(head)) {
            head++;
        }
        int emptyStack = tail + 1 == 10 ? 0 : tail + 1;
        while (emptyStack != head) {
            for (int i = 0; i < 4; i++) {
                textStack[emptyStack][i] = Text.of("");
            }
            emptyStack++;
            if (emptyStack == 10) {
                emptyStack = 0;
            }
        }
        update(textArray, loc);
        KSERecordsManager.getOperationStack().put(loc.toString(), this);
        save();
    }

    public Text[][] getTextStack() {
        return textStack;
    }

    public Text[] getTextStack(Integer now) {
        Text[] textArray = new Text[4];
        for (int i = 0; i < 4; i++) {
            textArray[i] = textStack[now][i];
        }
        return textArray;
    }

    public Integer getNow() {
        return now;
    }

    public Integer getTail() {
        return tail;
    }

    public Integer getHead() {
        return head;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(Player owner){
        this.owner = owner.getName();
    }

    public void save() throws IOException {
        KSERecordsManager.getInstance().saveOperationHistory();
    }

    public void setTail(int tail) {
        this.tail = tail;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public void setNow(Integer now) {
        this.now = now;
    }

    public void setOwner(String owner){
        owner = this.owner;
    }

    public void update(Text[] textArray, Location<World> loc) throws IOException {
        for (int i = 0; i < 4; i++) {
            textStack[now][i] = textArray[i];
        }
        KSERecordsManager.getOperationStack().put(loc.toString(), this);
        save();
    }

    public boolean isOwner(Player player) {
        return player.getName() == owner;
    }
}
