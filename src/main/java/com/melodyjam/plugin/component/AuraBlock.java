package com.melodyjam.plugin.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.melodyjam.plugin.ForgottenMelodiesPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.List;

public class AuraBlock implements Component<ChunkStore> {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public AuraBlock() {}

    public AuraBlock(String entityEffectId) {
        this.entityEffectId = entityEffectId;
    }

    @Nonnull
    public static final BuilderCodec<AuraBlock> CODEC = BuilderCodec.builder(AuraBlock.class, AuraBlock::new)
            .append(
                    new KeyedCodec<>("EntityEffectId", Codec.STRING), (o, i) -> o.entityEffectId = i, o -> o.entityEffectId
            )
            .addValidator(Validators.nonNull())
            .add()
        .build();

    protected String entityEffectId;

    private float timeSinceLastPulse;

    public static ComponentType<ChunkStore, AuraBlock> getComponentType() {
        return ForgottenMelodiesPlugin.instance().getAuraBlockComponentType();
    }

    public void runBlockAction(float dt, int x, int y, int z, ComponentAccessor<EntityStore> access) {
        timeSinceLastPulse += dt;
        if (timeSinceLastPulse < 0.5f) {
            return;
        }

        if (timeSinceLastPulse > 3.0f) {
            timeSinceLastPulse = 3.0f;
        }

        timeSinceLastPulse -= 0.5f;

        Vector3d centerPoint = new Vector3d(
            (float)x + 0.5f,
            (float)y + 0.5f,
            (float)z + 0.5f
        );

        List<Ref<EntityStore>> entities = TargetUtil.getAllEntitiesInCylinder(centerPoint, 20, 20, access);
        for(Ref<EntityStore> entityRef : entities) {
            Entity entity = EntityUtils.getEntity(entityRef, access);
            if (!(entity instanceof Player)) {
                continue;
            }

            EffectControllerComponent controller = access.getComponent(entityRef, EffectControllerComponent.getComponentType());
            if (controller == null) {
                continue;
            }

            EntityEffect effectAsset = EntityEffect.getAssetMap().getAsset(this.entityEffectId);
            if (effectAsset == null) {
                continue;
            }
            
            controller.addEffect(entityRef, effectAsset, access);
        }
    }

    public String getEntityEffectId() {
        return entityEffectId;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new AuraBlock(this.entityEffectId);
    }
}
