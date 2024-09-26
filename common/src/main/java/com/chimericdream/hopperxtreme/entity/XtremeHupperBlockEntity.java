package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.block.XtremeHupperBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.chimericdream.hopperxtreme.block.Hoppers.XTREME_HUPPER_BLOCK_ENTITY;


public class XtremeHupperBlockEntity extends LootableContainerBlockEntity implements Hopper {
    private final Box INPUT_AREA_SHAPE = (Box)Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).getBoundingBoxes().get(0);

    private final int cooldownInTicks;

    private static final int[][] AVAILABLE_SLOTS_CACHE = new int[54][];
    private DefaultedList<ItemStack> inventory;
    private int transferCooldown;
    private long lastTickTime;
    private Direction facing;

    public XtremeHupperBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 8);
    }

    public XtremeHupperBlockEntity(BlockPos pos, BlockState state, int cooldownInTicks) {
        super(XTREME_HUPPER_BLOCK_ENTITY.get(), pos, state);

        this.cooldownInTicks = cooldownInTicks;
        this.inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
        this.transferCooldown = -1;
        this.facing = (Direction) state.get(XtremeHupperBlock.FACING);
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }

        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }

        nbt.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public Box getInputAreaShape() {
        return INPUT_AREA_SHAPE;
    }

    public int size() {
        return this.inventory.size();
    }

    public ItemStack removeStack(int slot, int amount) {
        this.generateLoot(null);
        return Inventories.splitStack(this.getHeldStacks(), slot, amount);
    }

    public void setStack(int slot, ItemStack stack) {
        this.generateLoot(null);
        this.getHeldStacks().set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
    }

    public void setCachedState(BlockState state) {
        super.setCachedState(state);
        this.facing = state.get(XtremeHupperBlock.FACING);
    }

    protected Text getContainerName() {
        Block block = this.getCachedState().getBlock();

        if (block instanceof XtremeHupperBlock) {
            String hopperKey = ((XtremeHupperBlock) block).getBaseKey();
            return Text.translatable(String.format("container.%s", hopperKey));
        }

        return Text.translatable("container.hupper");
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, XtremeHupperBlockEntity blockEntity) {
        --blockEntity.transferCooldown;
        blockEntity.lastTickTime = world.getTime();

        if (!blockEntity.needsCooldown()) {
            blockEntity.setTransferCooldown(0);
            insertAndExtract(world, pos, state, blockEntity, () -> extract(world, blockEntity));
        }
    }

    private static int getCooldownForBlock(XtremeHupperBlockEntity blockEntity) {
        Block block = blockEntity.getCachedState().getBlock();

        if (block instanceof XtremeHupperBlock) {
            return ((XtremeHupperBlock) block).getCooldownInTicks();
        }

        return 8;
    }

    private static boolean insertAndExtract(World world, BlockPos pos, BlockState state, XtremeHupperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return false;
        }

        if (!blockEntity.needsCooldown() && state.get(XtremeHupperBlock.ENABLED)) {
            boolean bl = false;

            if (!blockEntity.isEmpty()) {
                bl = insert(world, pos, blockEntity);
            }

            if (!blockEntity.isFull()) {
                bl |= booleanSupplier.getAsBoolean();
            }

            if (bl) {
                blockEntity.setTransferCooldown(getCooldownForBlock(blockEntity));
                markDirty(world, pos, state);

                return true;
            }
        }

        return false;
    }

    private boolean isFull() {
        Iterator<?> var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack) var1.next();
        } while (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount());

        return false;
    }

    private static boolean insert(World world, BlockPos pos, XtremeHupperBlockEntity blockEntity) {
        Inventory inventory = getOutputInventory(world, pos, blockEntity);
        if (inventory == null) {
            return false;
        }

        Direction direction = blockEntity.facing.getOpposite();
        if (isInventoryFull(inventory, direction)) {
            return false;
        }

        for (int i = 0; i < blockEntity.size(); ++i) {
            ItemStack itemStack = blockEntity.getStack(i);

            if (!itemStack.isEmpty()) {
                int j = itemStack.getCount();
                ItemStack itemStack2 = transfer(blockEntity, inventory, blockEntity.removeStack(i, 1), direction);

                if (itemStack2.isEmpty()) {
                    inventory.markDirty();
                    return true;
                }

                itemStack.setCount(j);

                if (j == 1) {
                    blockEntity.setStack(i, itemStack);
                }
            }
        }

        return false;
    }

    private static int[] getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory sidedInventory) {
            return sidedInventory.getAvailableSlots(side);
        }

        int i = inventory.size();
        if (i < AVAILABLE_SLOTS_CACHE.length) {
            int[] is = AVAILABLE_SLOTS_CACHE[i];

            if (is != null) {
                return is;
            }

            int[] js = indexArray(i);
            AVAILABLE_SLOTS_CACHE[i] = js;

            return js;
        }

        return indexArray(i);
    }

    private static int[] indexArray(int size) {
        int[] is = new int[size];

        for (int i = 0; i < is.length; is[i] = i++) {
        }

        return is;
    }

    private static boolean isInventoryFull(Inventory inventory, Direction direction) {
        int[] is = getAvailableSlots(inventory, direction);
        int[] var3 = is;
        int var4 = is.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            int i = var3[var5];
            ItemStack itemStack = inventory.getStack(i);

            if (itemStack.getCount() < itemStack.getMaxCount()) {
                return false;
            }
        }

        return true;
    }

    public static boolean extract(World world, Hopper hupper) {
        BlockPos blockPos = BlockPos.ofFloored(hupper.getHopperX(), hupper.getHopperY(), hupper.getHopperZ());
        BlockState blockState = world.getBlockState(blockPos);
        Inventory inventory = getInputInventory(world, hupper, blockPos, blockState);

        if (inventory != null) {
            Direction direction = Direction.UP;
            int[] var11 = getAvailableSlots(inventory, direction);
            int var12 = var11.length;

            for (int var8 = 0; var8 < var12; ++var8) {
                int i = var11[var8];

                if (extract(hupper, inventory, i, direction)) {
                    return true;
                }
            }

            return false;
        }

        boolean bl = ((XtremeHupperBlockEntity) hupper).canBlockFromBelow() && blockState.isFullCube(world, blockPos) && !blockState.isIn(BlockTags.DOES_NOT_BLOCK_HOPPERS);
        if (!bl) {
            Iterator var6 = getInputItemEntities(world, hupper).iterator();

            while (var6.hasNext()) {
                ItemEntity itemEntity = (ItemEntity) var6.next();
                if (extract(hupper, itemEntity)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        ItemStack itemStack = inventory.getStack(slot);
        if (!itemStack.isEmpty() && canExtract(hopper, inventory, itemStack, slot, side)) {
            int i = itemStack.getCount();
            ItemStack itemStack2 = transfer(inventory, hopper, inventory.removeStack(slot, 1), null);

            if (itemStack2.isEmpty()) {
                inventory.markDirty();
                return true;
            }

            itemStack.setCount(i);
            if (i == 1) {
                inventory.setStack(slot, itemStack);
            }
        }

        return false;
    }

    public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
        boolean bl = false;

        ItemStack itemStack = itemEntity.getStack().copy();
        ItemStack itemStack2 = transfer(null, inventory, itemStack, null);

        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.setStack(ItemStack.EMPTY);
            itemEntity.discard();
        } else {
            itemEntity.setStack(itemStack2);
        }

        return bl;
    }

    public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
        int i;
        if (to instanceof SidedInventory sidedInventory) {
            if (side != null) {
                int[] is = sidedInventory.getAvailableSlots(side);

                for (i = 0; i < is.length && !stack.isEmpty(); ++i) {
                    stack = transfer(from, to, stack, is[i], side);
                }

                return stack;
            }
        }

        int j = to.size();

        for (i = 0; i < j && !stack.isEmpty(); ++i) {
            stack = transfer(from, to, stack, i, side);
        }

        return stack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        }

        if (inventory instanceof SidedInventory sidedInventory) {
            if (!sidedInventory.canInsert(slot, stack, side)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canExtract(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing) {
        if (!fromInventory.canTransferTo(hopperInventory, slot, stack)) {
            return false;
        }

        if (fromInventory instanceof SidedInventory sidedInventory) {
            if (!sidedInventory.canExtract(slot, stack, facing)) {
                return false;
            }
        }

        return true;
    }

    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = to.getStack(slot);

        if (canInsert(to, stack, slot, side)) {
            boolean bl = false;
            boolean bl2 = to.isEmpty();

            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
                bl = j > 0;
            }

            if (bl) {
                if (bl2 && to instanceof XtremeHupperBlockEntity hopperBlockEntity) {
                    if (!hopperBlockEntity.isDisabled()) {
                        int j = 0;

                        if (from instanceof XtremeHupperBlockEntity hopperBlockEntity2) {
                            if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
                                j = 1;
                            }
                        }

                        hopperBlockEntity.setTransferCooldown(getCooldownForBlock(hopperBlockEntity) - j);
                    }
                }

                to.markDirty();
            }
        }

        return stack;
    }

    @Nullable
    private static Inventory getOutputInventory(World world, BlockPos pos, XtremeHupperBlockEntity blockEntity) {
        return getInventoryAt(world, pos.offset(blockEntity.facing));
    }

    @Nullable
    private static Inventory getInputInventory(World world, Hopper hopper, BlockPos pos, BlockState state) {
        return getInventoryAt(world, pos, state, hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
    }

    public static List<ItemEntity> getInputItemEntities(World world, Hopper hopper) {
        Box box = hopper.getInputAreaShape().offset(hopper.getHopperX() - 0.5, hopper.getHopperY(), hopper.getHopperZ() - 0.5);
        return world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY);
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos pos) {
        return getInventoryAt(world, pos, world.getBlockState(pos), (double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5);
    }

    @Nullable
    private static Inventory getInventoryAt(World world, BlockPos pos, BlockState state, double x, double y, double z) {
        Inventory inventory = getBlockInventoryAt(world, pos, state);
        if (inventory == null) {
            inventory = getEntityInventoryAt(world, x, y, z);
        }

        return inventory;
    }

    @Nullable
    private static Inventory getBlockInventoryAt(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof InventoryProvider) {
            return ((InventoryProvider) block).getInventory(state, world, pos);
        }

        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory inventory) {
                if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    inventory = ChestBlock.getInventory((ChestBlock) block, state, world, pos, true);
                }

                return inventory;
            }
        }

        return null;
    }

    @Nullable
    private static Inventory getEntityInventoryAt(World world, double x, double y, double z) {
        List<Entity> list = world.getOtherEntities(null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES);
        return !list.isEmpty() ? (Inventory) list.get(world.random.nextInt(list.size())) : null;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
    }

    public double getHopperX() {
        return (double) this.pos.getX() + 0.5;
    }

    public double getHopperY() {
        return (double) this.pos.getY() - 0.5;
    }

    public double getHopperZ() {
        return (double) this.pos.getZ() + 0.5;
    }

    public boolean canBlockFromAbove() {
        return true;
    }

    public boolean canBlockFromBelow() {
        return true;
    }

    private void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean isDisabled() {
        return this.transferCooldown > this.cooldownInTicks;
    }

    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, XtremeHupperBlockEntity blockEntity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (!itemEntity.getStack().isEmpty() && entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(blockEntity.getInputAreaShape())) {
                insertAndExtract(world, pos, state, blockEntity, () -> extract(blockEntity, itemEntity));
            }
        }
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }
}
