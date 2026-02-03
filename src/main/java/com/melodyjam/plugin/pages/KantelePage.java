//The UI page and command function were inspired and modifed from from TroubleDev's tutorial at https://www.youtube.com/watch?v=cha7YFULwxY

package com.melodyjam.plugin.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class KantelePage extends InteractiveCustomUIPage<KantelePage.KanteleData> {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public KantelePage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, KanteleData.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        // Load the UI file
        // Path is relative to: src/main/resources/Common/UI/Custom/
        uiCommandBuilder.append("Pages/KanteleUI.ui");

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#HighD", EventData.of("Button", "HighD"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#HighC", EventData.of("Button", "HighC"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#HighB", EventData.of("Button", "HighB"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#HighA", EventData.of("Button", "HighA"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#G", EventData.of("Button", "G"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#F", EventData.of("Button", "F"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#E", EventData.of("Button", "E"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#LowD", EventData.of("Button", "LowD"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#LowC", EventData.of("Button", "LowC"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#LowB", EventData.of("Button", "LowB"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#LowA", EventData.of("Button", "LowA"));
    }


    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull KanteleData data) {
        if (data.button != null) {
            PlayerRef player = store.getComponent(ref, PlayerRef.getComponentType());
            if (player == null) {
                sendUpdate();
                return;
            }

            int soundIndex = SoundEvent.getAssetMap().getIndex(String.format("Note_%s", data.button));
            if (soundIndex < 0) {
                LOGGER.atWarning().log("Could not found asset for note %s", data.button);
                sendUpdate();
                return;
            }

            store.getExternalData().getWorld().execute(() -> {
                TransformComponent transform = store.getComponent(ref, EntityModule.get().getTransformComponentType());
                SoundUtil.playSoundEvent3d(ref, soundIndex, transform.getPosition(), store);
            });
        }

        sendUpdate();
    }

    public static class KanteleData {
        static final String KEY_BUTTON = "Button";

        public static final BuilderCodec<KanteleData> CODEC = BuilderCodec.<KanteleData>builder(KanteleData.class, KanteleData::new)
                .addField(new KeyedCodec<>(KEY_BUTTON, Codec.STRING), (d, s) -> d.button = s, d -> d.button)

                .build();

        private String button;
    }
}
