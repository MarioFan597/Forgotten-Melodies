package com.melodyjam.plugin.interaction;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.util.InteractionTarget;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.melodyjam.plugin.codec.validator.NullOr;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;

public class ReplaceHeldItemInteraction extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<ReplaceHeldItemInteraction> CODEC = BuilderCodec.builder(
        ReplaceHeldItemInteraction.class, ReplaceHeldItemInteraction::new, SimpleInstantInteraction.CODEC
    )
    .documentation("Replacing an item stack held in the target's hand with an identical one with a different item id. Will fail if the player's hand is empty.")
    .append(new KeyedCodec<>("ItemId", Codec.STRING), (i, s) -> i.itemId = s, i -> i.itemId)
    .documentation("The item id to put in the target's hand.")
    .addValidator(Validators.nonNull())
    .addValidator(Item.VALIDATOR_CACHE.getValidator().late())
    .add()
    .appendInherited(
            new KeyedCodec<>("Entity", InteractionTarget.CODEC), (o, i) -> o.entityTarget = i, o -> o.entityTarget, (o, p) -> o.entityTarget = p.entityTarget
    )
    .documentation("The entity to check.")
    .addValidator(Validators.nonNull())
    .add()
    .build();

    private String itemId;
    @Nonnull
    private InteractionTarget entityTarget = InteractionTarget.USER;

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

        ItemStack stack = inventory.getActiveHotbarItem();
        if (stack == null || itemId == null || itemId.isEmpty()) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        ItemStack replacement = new ItemStack(itemId, stack.getQuantity(), stack.getDurability(), stack.getMaxDurability(), stack.getMetadata());
        inventory.getHotbar().setItemStackForSlot(inventory.getActiveHotbarSlot(), replacement);

    }
}
