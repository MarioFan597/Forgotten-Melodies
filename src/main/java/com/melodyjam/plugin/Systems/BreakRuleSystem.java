package com.melodyjam.plugin.Systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.ArrayList;
import java.util.Arrays;

public class BreakRuleSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    final String worldName = "Portals_Resonant_Ruin";
    final String[] whitelist = new String[] {
            "Deco_SpiderWeb_Full",
            "Deco_SpiderWeb_Flat",
            "Deco_SpiderWeb",
            "Deco_Spider_Cocoon",
            "Furniture_Temple_Light_Pot",
            "Rubble_Marble",
            "Rubble_Marble_Medium",
            "Wood_Sticks"
    };

    public BreakRuleSystem() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void handle(
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl BreakBlockEvent event
    ) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        assert player != null;
        World world = player.getWorld();
        assert world != null;
        if (world.getName().contains(worldName)) {
            BlockType blockType = event.getBlockType();
            Item item = blockType.getItem();
            if (item != null) {
                String itemId = item.getId();
                if (Arrays.stream(whitelist).anyMatch(itemId::contains)) {
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
