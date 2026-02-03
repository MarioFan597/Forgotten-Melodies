//The UI page and command function were inspired and modifed from from TroubleDev's tutorial at https://www.youtube.com/watch?v=cha7YFULwxY

package com.melodyjam.plugin.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class KantelePage extends BasicCustomUIPage {

    public KantelePage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(@Nonnull UICommandBuilder cmd) {
        // Load the UI file
        // Path is relative to: src/main/resources/Common/UI/Custom/
        cmd.append("Pages/KanteleUI.ui");
    }
//
//    @Override
//    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, Data data) {
//        super.handleDataEvent(ref, store, data);
//
//        System.out.println("EVENT: " + data.value);
//
//        sendUpdate();
//    }
}
