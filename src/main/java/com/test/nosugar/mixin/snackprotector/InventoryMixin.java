package com.test.nosugar.mixin.snackprotector;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Inventory.class, priority = 0)
public class InventoryMixin {

    /*private static Player player_;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(Player player, CallbackInfo ci) {
        NonNullList<ItemStack> originalItems = ((InventoryAccessor) this).getItems();
        ProtectedNonNullList protectedItems = new ProtectedNonNullList(originalItems, ItemStack.EMPTY, player);
        ((InventoryAccessor) this).setItems(protectedItems);

        NonNullList<ItemStack> originalArmor = ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList protectedArmor = new ProtectedNonNullList(originalArmor, ItemStack.EMPTY, player);
        ((InventoryAccessor) this).setArmor(protectedArmor);

        NonNullList<ItemStack> originalOffhand = ((InventoryAccessor) this).getOffhand();
        ProtectedNonNullList protectedOffhand = new ProtectedNonNullList(originalOffhand, ItemStack.EMPTY, player);
        ((InventoryAccessor) this).setOffhand(protectedOffhand);

        List<NonNullList<ItemStack>> newCompartments = ImmutableList.of(
                protectedItems,
                protectedArmor,
                protectedOffhand
        );
        player_ = player;
        ((InventoryAccessor) this).setCompartments(newCompartments);
    }

    @Inject(method = "dropAll", at = @At("HEAD"), cancellable = true)
    private void ondropAll(CallbackInfo ci) {
        if (SnackArmor.SnackProtector.isFullSet(player_)) {
            ci.cancel();
        }
    }

    @Inject(method = "setPickedItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private void onSetPickedItemStart(ItemStack stack, CallbackInfo ci) {
        ProtectedNonNullList list = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        list.setEditingAllowed(true);
    }

    @Inject(method = "setPickedItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void onSetPickedItemEnd(ItemStack stack, CallbackInfo ci) {
        ProtectedNonNullList list = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        list.setEditingAllowed(false);
    }

    @Inject(method = "pickSlot(I)V", at = @At("HEAD"))
    private void onPickSlotStart(int index, CallbackInfo ci) {
        ProtectedNonNullList list = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        list.setEditingAllowed(true);
    }

    @Inject(method = "pickSlot(I)V", at = @At("RETURN"))
    private void onPickSlotEnd(int index, CallbackInfo ci) {
        ProtectedNonNullList list = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        list.setEditingAllowed(false);
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
    private void onAddStart(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ProtectedNonNullList list = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        list.setEditingAllowed(true);
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void onAddEnd(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ProtectedNonNullList list = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        list.setEditingAllowed(false);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/ListTag;)V", at = @At("HEAD"))
    private void onLoadStart(ListTag tag, CallbackInfo ci) {
        ProtectedNonNullList itemsList = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        ProtectedNonNullList armorList = (ProtectedNonNullList) ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList offhandList = (ProtectedNonNullList) ((InventoryAccessor) this).getOffhand();

        itemsList.setIsLoadContext(true);
        armorList.setIsLoadContext(true);
        offhandList.setIsLoadContext(true);

        itemsList.setEditingAllowed(true);
        armorList.setEditingAllowed(true);
        offhandList.setEditingAllowed(true);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/ListTag;)V", at = @At("RETURN"))
    private void onLoadEnd(ListTag tag, CallbackInfo ci) {
        ProtectedNonNullList itemsList = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        ProtectedNonNullList armorList = (ProtectedNonNullList) ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList offhandList = (ProtectedNonNullList) ((InventoryAccessor) this).getOffhand();

        itemsList.setEditingAllowed(false);
        armorList.setEditingAllowed(false);
        offhandList.setEditingAllowed(false);

        itemsList.setIsLoadContext(false);
        armorList.setIsLoadContext(false);
        offhandList.setIsLoadContext(false);
    }

    /*@Inject(method = "clearContent()V", at = @At("HEAD"))
    private void onClearStart(CallbackInfo ci) {
        ProtectedNonNullList itemsList = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        ProtectedNonNullList armorList = (ProtectedNonNullList) ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList offhandList = (ProtectedNonNullList) ((InventoryAccessor) this).getOffhand();
        itemsList.setEditingAllowed(true);
        armorList.setEditingAllowed(true);
        offhandList.setEditingAllowed(true);
    }

    @Inject(method = "clearContent()V", at = @At("RETURN"))
    private void onClearEnd(CallbackInfo ci) {
        ProtectedNonNullList itemsList = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        ProtectedNonNullList armorList = (ProtectedNonNullList) ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList offhandList = (ProtectedNonNullList) ((InventoryAccessor) this).getOffhand();
        itemsList.setEditingAllowed(false);
        armorList.setEditingAllowed(false);
        offhandList.setEditingAllowed(false);
    }

    @Inject(method = "addResource(ILnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"))
    private void onAddResourceStart(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        ProtectedNonNullList itemsList = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        ProtectedNonNullList armorList = (ProtectedNonNullList) ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList offhandList = (ProtectedNonNullList) ((InventoryAccessor) this).getOffhand();

        itemsList.setIsLoadContext(true);
        armorList.setIsLoadContext(true);
        offhandList.setIsLoadContext(true);

        itemsList.setEditingAllowed(true);
        armorList.setEditingAllowed(true);
        offhandList.setEditingAllowed(true);
    }

    @Inject(method = "addResource(ILnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"))
    private void onAddResourceEnd(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        ProtectedNonNullList itemsList = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        ProtectedNonNullList armorList = (ProtectedNonNullList) ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList offhandList = (ProtectedNonNullList) ((InventoryAccessor) this).getOffhand();

        itemsList.setEditingAllowed(false);
        armorList.setEditingAllowed(false);
        offhandList.setEditingAllowed(false);

        itemsList.setIsLoadContext(false);
        armorList.setIsLoadContext(false);
        offhandList.setIsLoadContext(false);
    }

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private void onSetItemStart(int slot, ItemStack stack, CallbackInfo ci) {
        ProtectedNonNullList itemsList = (ProtectedNonNullList) ((InventoryAccessor) this).getItems();
        ProtectedNonNullList armorList = (ProtectedNonNullList) ((InventoryAccessor) this).getArmor();
        ProtectedNonNullList offhandList = (ProtectedNonNullList) ((InventoryAccessor) this).getOffhand();
        ProtectedNonNullList targetList = null;

        if (slot >= 0 && slot < itemsList.size()) {
            targetList = itemsList;
        } else if (slot >= 100 && slot < 100 + armorList.size()) {
            targetList = armorList;
        } else if (slot >= 150 && slot < 150 + offhandList.size()) {
            targetList = offhandList;
        }

        if (targetList != null && targetList.isProtectionActive()) {
            ci.cancel();
        }
    }*/
}