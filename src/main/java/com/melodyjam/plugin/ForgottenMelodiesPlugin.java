package com.melodyjam.plugin;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.melodyjam.plugin.interaction.WieldedItemConditionInteraction;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ForgottenMelodiesPlugin extends JavaPlugin {
    public ForgottenMelodiesPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();

        this.getCodecRegistry(Interaction.CODEC).register("WieldedItemCondition", WieldedItemConditionInteraction.class, WieldedItemConditionInteraction.CODEC);
    }
}
