package cn.thecoldworld.thecoldworldlib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public final class ItemUtil {
    private ItemUtil() {
    }

    public static boolean giveItem(final Item item, final int count, final ServerPlayerEntity entity) {
        return giveItem(item, count, entity, false);
    }

    public static boolean giveItem(final Item item, final int count, final ServerPlayerEntity entity, boolean dropIfFull) {
        if (count <= 0) return false;
        PlayerInventory inventory = entity.getInventory();
        ItemStack defaultStack = item.getDefaultStack();
        if (item.getMaxCount() == 1) {
            int[] indexes = dropIfFull
                    ? getEmptySlotsIndex(inventory, count, PlayerInventory.NOT_FOUND)
                    : getEmptySlotsIndex(inventory, count);
            if (indexes == null) return false;

            defaultStack.setCount(1);
            for (int i = 0; i < Arrays.stream(indexes).filter(k -> k == PlayerInventory.NOT_FOUND).count(); i++) {
                if (!dropOnEntity(defaultStack.copy(), entity, true)) return false;
            }
            for (int index : indexes) {
                if (index == PlayerInventory.NOT_FOUND) continue;
                inventory.setStack(index, defaultStack.copy());
            }
            return true;
        } else try {
            Map<Integer, Integer> map = new LinkedHashMap<>();//left :slot index ,right:insert count
            int leftcount = count;
            for (int i = 0; i < inventory.getMainStacks().size() && leftcount > 0; i++) {
                ItemStack slot = inventory.getMainStacks().get(i);
                if (ItemStack.areItemsAndComponentsEqual(slot, defaultStack)) {
                    int willinsert = Math.min(slot.getMaxCount() - slot.getCount(), leftcount);
                    if (willinsert == 0) continue;
                    map.put(i, willinsert);
                    leftcount -= willinsert;
                }
            }
            if (leftcount > 0) {
                int maxCount = defaultStack.getMaxCount();
                boolean b = leftcount % maxCount == 0;
                int[] emptySlotsIndex = getEmptySlotsIndex(inventory, b
                                ? leftcount / maxCount
                                : leftcount / maxCount + 1,
                        PlayerInventory.NOT_FOUND);
                if (emptySlotsIndex == null) return false;
                if (b) {
                    for (int slotsIndex : emptySlotsIndex) {
                        if (slotsIndex != PlayerInventory.NOT_FOUND) map.put(slotsIndex, maxCount);
                        else
                            map.put(PlayerInventory.NOT_FOUND, map.getOrDefault(PlayerInventory.NOT_FOUND, 0) + maxCount);
                    }
                } else {
                    for (int i = 0; i < emptySlotsIndex.length - 1; i++) {
                        if (emptySlotsIndex[i] != PlayerInventory.NOT_FOUND) map.put(emptySlotsIndex[i], maxCount);
                        else
                            map.put(PlayerInventory.NOT_FOUND, map.getOrDefault(PlayerInventory.NOT_FOUND, 0) + maxCount);
                    }
                    int last = emptySlotsIndex[emptySlotsIndex.length - 1];
                    if (last != PlayerInventory.NOT_FOUND) map.put(last, leftcount % maxCount);
                    else
                        map.put(PlayerInventory.NOT_FOUND, map.getOrDefault(PlayerInventory.NOT_FOUND, 0) + leftcount % maxCount);
                }
            }
            if (map.containsKey(PlayerInventory.NOT_FOUND) && !dropIfFull) return false;
            else {
                if (map.containsKey(PlayerInventory.NOT_FOUND)) {
                    ItemStack stack = defaultStack.copy();
                    stack.setCount(map.get(PlayerInventory.NOT_FOUND));
                    if (!dropOnEntity(stack, entity, true)) return false;
                }
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {//left :slot index ,right:insert count
                    if (entry.getKey() == PlayerInventory.NOT_FOUND) continue;
                    defaultStack.setCount(entry.getValue());
                    inventory.setStack(entry.getKey(), defaultStack.copy());
                }
                return true;
            }
        } catch (Throwable igonred) {
            return false;
        }
    }

    public static int getEmptySlotsCount(@NotNull Inventory inventory) {
        if (inventory.isEmpty()) return 0;
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) count++;
        }
        return count;
    }

    public static int getEmptySlotsCount(@NotNull PlayerInventory inventory) {
        if (inventory.isEmpty()) return 0;
        int count = 0;
        for (int i = 0; i < inventory.getMainStacks().size(); i++) {
            if (inventory.getStack(i).isEmpty()) count++;
        }
        if (inventory.getStack(PlayerInventory.OFF_HAND_SLOT).isEmpty()) count++;
        return count;
    }

    /**
     * @return array size in empty slots count if inventory is not empty
     */
    public static int @Nullable [] getEmptySlotsIndex(@NotNull Inventory inventory) {
        if (inventory.isEmpty()) return null;
        LinkedList<Integer> indexes = new LinkedList<>();
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) indexes.add(i);
        }
        if (indexes.isEmpty()) return null;
        else {
            int[] r = new int[indexes.size()];
            int i = 0;
            for (Integer integer : indexes) {
                r[i++] = integer;
            }
            return r;
        }
    }

    /**
     * @return array size in empty slots count if inventory is not empty
     */
    public static int @Nullable [] getEmptySlotsIndex(@NotNull PlayerInventory inventory) {
        if (inventory.isEmpty()) return null;
        LinkedList<Integer> indexes = new LinkedList<>();
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) indexes.add(i);
        }
        if (inventory.getStack(PlayerInventory.OFF_HAND_SLOT).isEmpty()) indexes.add(PlayerInventory.OFF_HAND_SLOT);
        if (indexes.isEmpty()) return null;
        else {
            int[] r = new int[indexes.size()];
            int i = 0;
            for (Integer integer : indexes) {
                r[i++] = integer;
            }
            return r;
        }
    }

    /**
     * @return array size in {@code slotCount} if inventory is not empty and  sufficient number of empty slots,otherwise {@code null}
     * @see #getEmptySlotsIndex(Inventory) all empty slots mehtod
     */
    public static int @Nullable [] getEmptySlotsIndex(@NotNull Inventory inventory, int slotCount) {
        if (inventory.isEmpty()) return null;
        int[] indexes = new int[slotCount];
        Arrays.fill(indexes, PlayerInventory.NOT_FOUND);
        int index = 0;
        for (int i = 0; i < inventory.size() && index < slotCount; i++) {
            if (inventory.getStack(i).isEmpty()) indexes[index++] = i;
        }
        if (Arrays.stream(indexes).anyMatch(i -> i == PlayerInventory.NOT_FOUND)) return null;
        else return indexes;
    }

    /**
     * @return array size in {@code slotCount} if inventory is not empty and  sufficient number of empty slots,otherwise {@code null}
     * @see #getEmptySlotsIndex(PlayerInventory) all empty slots method
     */
    public static int @Nullable [] getEmptySlotsIndex(@NotNull PlayerInventory inventory, int slotCount) {
        if (inventory.isEmpty()) return null;
        int[] indexes = new int[slotCount];
        Arrays.fill(indexes, PlayerInventory.NOT_FOUND);
        int index = 0;
        for (int i = 0; i < inventory.getMainStacks().size() && index < slotCount; i++) {
            if (inventory.getMainStacks().get(i).isEmpty()) indexes[index++] = i;
        }
        if (index < slotCount && inventory.getStack(PlayerInventory.OFF_HAND_SLOT).isEmpty())
            indexes[index] = PlayerInventory.OFF_HAND_SLOT;
        if (Arrays.stream(indexes).anyMatch(i -> i == PlayerInventory.NOT_FOUND)) return null;
        else return indexes;
    }

    /**
     * @param defaultIndex will be filled into the contents of the array if there are not enough empty slots
     * @return array size in {@code  slotCount} if inventory is not empty ,otherwise {@code null}
     * @see #getEmptySlotsIndex(Inventory, int) without default
     */
    public static int @Nullable [] getEmptySlotsIndex(@NotNull Inventory inventory, int slotCount, int defaultIndex) {
        if (inventory.isEmpty()) return null;
        int[] indexes = new int[slotCount];
        Arrays.fill(indexes, PlayerInventory.NOT_FOUND);
        int index = 0;
        for (int i = 0; i < inventory.size() && index < slotCount; i++) {
            if (inventory.getStack(i).isEmpty()) indexes[index++] = i;
        }
        if (defaultIndex != PlayerInventory.NOT_FOUND && Arrays.stream(indexes).anyMatch(i -> i == PlayerInventory.NOT_FOUND)) {
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] == PlayerInventory.NOT_FOUND) indexes[i] = defaultIndex;
            }
        }
        return indexes;
    }

    /**
     * @param defaultIndex will be filled into the contents of the array if there are not enough empty slots
     * @return array size in {@code  slotCount} if inventory is not empty ,otherwise {@code null}
     * @see #getEmptySlotsIndex(PlayerInventory, int) without default
     */
    public static int @Nullable [] getEmptySlotsIndex(@NotNull PlayerInventory inventory, int emptySlotCount, int defaultIndex) {
        if (inventory.isEmpty()) return null;
        int[] indexes = new int[emptySlotCount];
        Arrays.fill(indexes, PlayerInventory.NOT_FOUND);
        int index = 0;
        for (int i = 0; i < inventory.getMainStacks().size() && index < emptySlotCount; i++) {
            if (inventory.getMainStacks().get(i).isEmpty()) indexes[index++] = i;
        }
        if (IterUtil.oneMatch(indexes, i -> i == PlayerInventory.NOT_FOUND) && inventory.getStack(PlayerInventory.OFF_HAND_SLOT).isEmpty())
            indexes[emptySlotCount - 1] = PlayerInventory.OFF_HAND_SLOT;
        if (defaultIndex != PlayerInventory.NOT_FOUND && Arrays.stream(indexes).anyMatch(i -> i == PlayerInventory.NOT_FOUND)) {
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] == PlayerInventory.NOT_FOUND) indexes[i] = defaultIndex;
            }
        }
        return indexes;
    }

    /**
     * drop item on the entity position(no velocity,no pick up delay)
     *
     * @param stack    itemstack(can exceed item stack limit)
     * @param setOwner set the owner of the drop item entity
     * @return {@code true} if success ,otherwise {@code false}
     */
    public static boolean dropOnEntity(@NotNull ItemStack stack, Entity entity, boolean setOwner) {
        if (stack.isEmpty()) return false;
        int maxCount = stack.getMaxCount();
        int count = stack.getCount();
        if (count > maxCount) {
            int counts = count / maxCount;
            int left = count % maxCount;
            ItemStack stack1 = stack.copy();
            stack1.setCount(maxCount);
            for (int i = 0; i < counts; i++) {
                ItemEntity itemEntity = new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), stack1.copy());
                if (setOwner) itemEntity.setThrower(entity);
                if (!entity.getWorld().spawnEntity(itemEntity)) return false;
            }
            if (left > 0) {
                stack1.setCount(left);
                ItemEntity itemEntity = new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), stack1);
                if (setOwner) itemEntity.setThrower(entity);
                return entity.getWorld().spawnEntity(itemEntity);
            }
            return true;

        } else {
            ItemEntity itemEntity = new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), stack.copy());
            if (setOwner) itemEntity.setThrower(entity);
            return entity.getWorld().spawnEntity(itemEntity);
        }
    }

    /**
     * @param excludedSlotIndex the slot index which will not return result
     * @return {@code PlayerInventory.NOT_FOUND} if {@code inventory} is empty,otherwise the index of first empty slot in the inventory
     * @see #getEmptySlotsIndex(Inventory, int) Multiple method
     */
    public int getInventoryEmptySlot(@NotNull Inventory inventory, int... excludedSlotIndex) {
        if (inventory.isEmpty()) return PlayerInventory.NOT_FOUND;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) {
                int finalI = i;
                if (Arrays.stream(excludedSlotIndex).noneMatch(j -> j == finalI)) return i;
            }
        }
        return PlayerInventory.NOT_FOUND;
    }

    /**
     * @param excludedSlotIndex the slot index which will not return result
     * @return {@code PlayerInventory.NOT_FOUND} if {@code inventory} is empty,otherwise the index of first empty slot in the inventory(include offhand)
     * @see #getEmptySlotsIndex(PlayerInventory, int) Multiple method
     */
    public int getInventoryEmptySlot(@NotNull PlayerInventory inventory, int... excludedSlotIndex) {
        if (inventory.isEmpty()) return PlayerInventory.NOT_FOUND;
        for (int i = 0; i < inventory.getMainStacks().size(); i++) {
            if (inventory.getMainStacks().get(i).isEmpty()) {
                int finalI = i;
                if (Arrays.stream(excludedSlotIndex).noneMatch(j -> j == finalI)) return i;
            }
        }
        if (Arrays.stream(excludedSlotIndex).noneMatch(j -> j == PlayerInventory.OFF_HAND_SLOT)) {
            return inventory.getStack(PlayerInventory.OFF_HAND_SLOT).isEmpty()
                    ? PlayerInventory.OFF_HAND_SLOT
                    : PlayerInventory.NOT_FOUND;
        }
        return PlayerInventory.NOT_FOUND;
    }
}
