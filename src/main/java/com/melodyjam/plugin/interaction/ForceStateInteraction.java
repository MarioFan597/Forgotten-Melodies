package com.melodyjam.plugin.interaction;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class ForceStateInteraction extends SimpleBlockInteraction {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Nonnull
    public static final BuilderCodec<ForceStateInteraction> CODEC = BuilderCodec.builder(
        ForceStateInteraction.class, ForceStateInteraction::new, SimpleBlockInteraction.CODEC
    )
    .documentation("Changes the state of the target block to another state regardless of the current state")
    .appendInherited(
        new KeyedCodec<>("ChangeTo", Codec.STRING),
        (i, o) -> i.changeTo = o,
        i -> i.changeTo,
        (o, p) -> o.changeTo = p.changeTo
    )
    .documentation("The block state to change to. `\"default\"` can be used for the initial state of a block.")
    .add()
    .appendInherited(
            new KeyedCodec<>("UpdateBlockState", Codec.BOOLEAN),
            (o, i) -> o.updateBlockState = i,
            o -> o.updateBlockState,
            (o, p) -> o.updateBlockState = p.updateBlockState
    )
    .add()
    .build();

    protected boolean updateBlockState = false;
    protected String changeTo;

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
        if (chunk == null) {
            return;
        }

        String targetVal = this.changeTo;
        if (targetVal == null) {
            targetVal = "default";
        }

        BlockType current = chunk.getBlockType(targetBlock);

        String currentState = current.getStateForBlock(current);
        if (currentState == null) {
            currentState = "default";
        }

        if (currentState.equals(targetVal)) {
            return;
        }

        String newBlock = current.getBlockKeyForState(targetVal);

        if (newBlock == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        int newBlockId = BlockType.getAssetMap().getIndex(newBlock);
        if (newBlockId == Integer.MIN_VALUE) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        BlockType newBlockType = BlockType.getAssetMap().getAsset(newBlockId);
        int rotation = chunk.getRotationIndex(targetBlock.x, targetBlock.y, targetBlock.z);
        int settings = 260;
        if (!this.updateBlockState) {
            settings |= 2;
        }

        chunk.setBlock(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), newBlockId, newBlockType, rotation, 0, settings);
        BlockType interactionStateBlock = current.getBlockForState(targetVal);
        if (interactionStateBlock == null) {
            return;
        }

        int soundEvent = interactionStateBlock.getInteractionSoundEventIndex();
        if (soundEvent == 0) {
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();
        SoundUtil.playSoundEvent3d(ref, soundEvent, targetBlock.x + 0.5, targetBlock.y + 0.5, targetBlock.z + 0.5, commandBuffer);
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }
}
