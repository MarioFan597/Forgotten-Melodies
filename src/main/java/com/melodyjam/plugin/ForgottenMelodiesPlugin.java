package com.melodyjam.plugin;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.melodyjam.plugin.Systems.AuraBlockInitializer;
import com.melodyjam.plugin.Systems.AuraBlockSystem;
import com.melodyjam.plugin.component.AuraBlock;
import com.melodyjam.plugin.interaction.ForceStateInteraction;
import com.melodyjam.plugin.interaction.ReplaceHeldItemInteraction;
import com.melodyjam.plugin.interaction.WieldedItemConditionInteraction;
import com.melodyjam.plugin.Systems.BreakRuleSystem;
import com.melodyjam.plugin.Systems.PlaceRuleSystem;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ForgottenMelodiesPlugin extends JavaPlugin {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    protected static ForgottenMelodiesPlugin instance;

    private ComponentType<ChunkStore, AuraBlock> auraBlockComponentType;

    public static ForgottenMelodiesPlugin instance() {
        return instance;
    }

    public ComponentType<ChunkStore, AuraBlock> getAuraBlockComponentType() {
        return auraBlockComponentType;
    }

    public ForgottenMelodiesPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();
        instance = this;

        this.auraBlockComponentType = this.getChunkStoreRegistry().registerComponent(AuraBlock.class, "AuraBlock", AuraBlock.CODEC);

        this.getCodecRegistry(Interaction.CODEC).register("WieldedItemCondition", WieldedItemConditionInteraction.class, WieldedItemConditionInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("ForceState", ForceStateInteraction.class, ForceStateInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("ReplaceHeldItem", ReplaceHeldItemInteraction.class, ReplaceHeldItemInteraction.CODEC);
		
        // Systems that disable building and breaking inside the dungeon.
        getEntityStoreRegistry().registerSystem(new BreakRuleSystem());
        getEntityStoreRegistry().registerSystem(new PlaceRuleSystem());
    }

    @Override
    protected void start() {
        this.getChunkStoreRegistry().registerSystem(new AuraBlockSystem());
        this.getChunkStoreRegistry().registerSystem(new AuraBlockInitializer());
    }
}
