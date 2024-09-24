package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.XtremeMultiHopperBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.hopperxtreme.block.Hoppers.XTREME_MULTI_HOPPER_BLOCK_ENTITY;

public class XtremeMultiHopperBlock extends BlockWithEntity {
    public static final MapCodec<XtremeMultiHopperBlock> CODEC = createCodec(XtremeMultiHopperBlock::create);

    public static final BooleanProperty ENABLED;
    public static final BooleanProperty NORTH_CONNECTED;
    public static final BooleanProperty SOUTH_CONNECTED;
    public static final BooleanProperty EAST_CONNECTED;
    public static final BooleanProperty WEST_CONNECTED;
    public static final BooleanProperty DOWN_CONNECTED;

    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape OUTSIDE_SHAPE;
    public static final VoxelShape INSIDE_SHAPE;

    private static final VoxelShape DEFAULT_SHAPE;
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;

    private static final VoxelShape DOWN_RAYCAST_SHAPE;
    private static final VoxelShape EAST_RAYCAST_SHAPE;
    private static final VoxelShape NORTH_RAYCAST_SHAPE;
    private static final VoxelShape SOUTH_RAYCAST_SHAPE;
    private static final VoxelShape WEST_RAYCAST_SHAPE;

    static {
        ENABLED = Properties.ENABLED;
        NORTH_CONNECTED = BooleanProperty.of("north_connected");
        SOUTH_CONNECTED = BooleanProperty.of("south_connected");
        EAST_CONNECTED = BooleanProperty.of("east_connected");
        WEST_CONNECTED = BooleanProperty.of("west_connected");
        DOWN_CONNECTED = BooleanProperty.of("down_connected");

        TOP_SHAPE = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
        INSIDE_SHAPE = createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);

        DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);
        DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));

        DOWN_RAYCAST_SHAPE = INSIDE_SHAPE;
        EAST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }

    public MapCodec<XtremeMultiHopperBlock> getCodec() {
        return CODEC;
    }

    private final int cooldownInTicks;
    private final String baseKey;

    static XtremeMultiHopperBlock create(Settings settings) {
        return new XtremeMultiHopperBlock(8, "default");
    }

    public XtremeMultiHopperBlock(int cooldownInTicks, String baseKey) {
        super(Settings.copy(Blocks.HOPPER).mapColor(MapColor.STONE_GRAY).requiresTool().strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).nonOpaque());

        this.cooldownInTicks = cooldownInTicks;
        this.baseKey = baseKey;

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(NORTH_CONNECTED, false)
                .with(SOUTH_CONNECTED, false)
                .with(EAST_CONNECTED, false)
                .with(WEST_CONNECTED, false)
                .with(DOWN_CONNECTED, false)
                .with(ENABLED, true)
        );
    }

    public int getCooldownInTicks() {
        return cooldownInTicks;
    }

    public String getBaseKey() {
        return baseKey;
    }

    public static BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH_CONNECTED;
            case SOUTH -> SOUTH_CONNECTED;
            case EAST -> EAST_CONNECTED;
            case WEST -> WEST_CONNECTED;
            default -> DOWN_CONNECTED;
        };
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        List<VoxelShape> parts = new ArrayList<>();

        if (state.get(NORTH_CONNECTED)) {
            parts.add(NORTH_SHAPE);
        }
        if (state.get(SOUTH_CONNECTED)) {
            parts.add(SOUTH_SHAPE);
        }
        if (state.get(EAST_CONNECTED)) {
            parts.add(EAST_SHAPE);
        }
        if (state.get(WEST_CONNECTED)) {
            parts.add(WEST_SHAPE);
        }
        if (state.get(DOWN_CONNECTED)) {
            parts.add(DOWN_SHAPE);
        }

        return VoxelShapes.union(DEFAULT_SHAPE, parts.toArray(new VoxelShape[0]));
    }

    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        List<VoxelShape> parts = new ArrayList<>();

        if (state.get(NORTH_CONNECTED)) {
            parts.add(NORTH_RAYCAST_SHAPE);
        }
        if (state.get(SOUTH_CONNECTED)) {
            parts.add(SOUTH_RAYCAST_SHAPE);
        }
        if (state.get(EAST_CONNECTED)) {
            parts.add(EAST_RAYCAST_SHAPE);
        }
        if (state.get(WEST_CONNECTED)) {
            parts.add(WEST_RAYCAST_SHAPE);
        }
        if (state.get(DOWN_CONNECTED)) {
            parts.add(DOWN_RAYCAST_SHAPE);
        }

        return VoxelShapes.union(INSIDE_SHAPE, parts.toArray(new VoxelShape[0]));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new XtremeMultiHopperBlockEntity(pos, state, cooldownInTicks);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, XTREME_MULTI_HOPPER_BLOCK_ENTITY.get(), XtremeMultiHopperBlockEntity::serverTick);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.updateEnabled(world, pos, state);
        }
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof XtremeMultiHopperBlockEntity) {
                player.openHandledScreen((XtremeMultiHopperBlockEntity)blockEntity);
                player.incrementStat(Stats.INSPECT_HOPPER);
            }

            return ActionResult.CONSUME;
        }
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean bl = !world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get(ENABLED)) {
            world.setBlockState(pos, (BlockState)state.with(ENABLED, bl), 2);
        }

    }

    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ENABLED, NORTH_CONNECTED, SOUTH_CONNECTED, EAST_CONNECTED, WEST_CONNECTED, DOWN_CONNECTED});
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof XtremeMultiHopperBlockEntity) {
            XtremeMultiHopperBlockEntity.onEntityCollided(world, pos, state, entity, (XtremeMultiHopperBlockEntity)blockEntity);
        }

    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}