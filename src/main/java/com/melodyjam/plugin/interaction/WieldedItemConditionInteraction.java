package com.melodyjam.plugin.interaction;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.util.InteractionTarget;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.melodyjam.plugin.codec.validator.NullOr;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class WieldedItemConditionInteraction extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<WieldedItemConditionInteraction> CODEC = BuilderCodec.builder(
            WieldedItemConditionInteraction.class, WieldedItemConditionInteraction::new, SimpleInstantInteraction.CODEC
    ).documentation("Interaction that is successful if an entity has a specific item in the active hotbar slot")
    .append(new KeyedCodec<>("ItemId", Codec.STRING), (i, s) -> i.itemId = s, i -> i.itemId)
    .documentation("The id of the item to check for. If null, checks for an empty hand.")
    .addValidator(new NullOr<>(Item.VALIDATOR_CACHE.getValidator()))
    .add()
    .appendInherited(
        new KeyedCodec<>("Entity", InteractionTarget.CODEC), (o, i) -> o.entityTarget = i, o -> o.entityTarget, (o, p) -> o.entityTarget = p.entityTarget
    )
    .documentation("The entity to check.")
    .addValidator(Validators.nonNull())
    .add()
    .append(new KeyedCodec<>("MinimumQuantity", Codec.INTEGER), (p, i) -> p.minimumQuantity = i, p -> p.minimumQuantity)
    .documentation("If included, the minimum number of items that must be in the stack.")
    .addValidator(new NullOr<>(Validators.greaterThan(0)))
    .add()
    .append(new KeyedCodec<>("MaximumQuantity", Codec.INTEGER), (p, i) -> p.maximumQuantity = i, p -> p.maximumQuantity)
    .documentation("If included, the maximum number of items that can be in the stack.")
    .addValidator(new NullOr<>(Validators.greaterThan(0)))
    .add()
    .build();

    @Nonnull
    private InteractionTarget entityTarget = InteractionTarget.USER;
    private String itemId;
    private Integer minimumQuantity;
    private Integer maximumQuantity;

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Ref<EntityStore> targetRef = this.entityTarget.getEntity(context, ref);

        if (!(EntityUtils.getEntity(targetRef, context.getCommandBuffer()) instanceof LivingEntity livingEntity)) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        Inventory inventory = livingEntity.getInventory();
        if (inventory == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        ItemStack active = inventory.getActiveHotbarItem();
        if (active == null) {
            if (this.itemId != null) {
                // Empty hand but we were expecting something
                context.getState().state = InteractionState.Failed;
            }

            return;
        }

        if ((!active.getItemId().equals(this.itemId)) ||
            (this.minimumQuantity != null && active.getQuantity() < this.minimumQuantity) ||
            (this.maximumQuantity != null && active.getQuantity() > this.maximumQuantity)) {
            context.getState().state = InteractionState.Failed;
        }
    }

    @Nonnull
    @Override
    public String toString() {
        return "WieldedItemConditionInteraction(entityTarget="
                + this.entityTarget
                +", itemId="
                + this.itemId
                +", minimumQuantity="
                +this.minimumQuantity
                +", maximumQuantity="
                +this.maximumQuantity
                +") "
                + super.toString();
    }
}
