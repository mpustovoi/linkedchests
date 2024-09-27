package fuzs.linkedchests.neoforge;

import fuzs.linkedchests.LinkedChests;
import fuzs.linkedchests.data.ModBlockLootProvider;
import fuzs.linkedchests.data.ModBlockTagProvider;
import fuzs.linkedchests.data.ModItemTagProvider;
import fuzs.linkedchests.data.ModRecipeProvider;
import fuzs.linkedchests.init.ModRegistry;
import fuzs.linkedchests.world.level.block.entity.DyeChannel;
import fuzs.linkedchests.world.level.block.entity.DyeChannelManager;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.api.init.v3.capability.NeoForgeCapabilityHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

@Mod(LinkedChests.MOD_ID)
public class LinkedChestsNeoForge {

    public LinkedChestsNeoForge() {
        ModConstructor.construct(LinkedChests.MOD_ID, LinkedChests::new);
        NeoForgeCapabilityHelper.registerBlockEntityContainer(ModRegistry.LINKED_CHEST_BLOCK_ENTITY);
        registerItemContainer((ItemStack itemStack, Void aVoid) -> {
            DyeChannel dyeChannel = itemStack.getOrDefault(ModRegistry.DYE_CHANNEL_DATA_COMPONENT_TYPE.value(),
                    DyeChannel.DEFAULT
            );
            NonNullList<ItemStack> items = DyeChannelManager.getStorage(dyeChannel).items();
            return new ItemStackHandler(items);
        }, ModRegistry.LINKED_STORAGE_ITEM);
        DataProviderHelper.registerDataProviders(LinkedChests.MOD_ID, ModBlockLootProvider::new,
                ModBlockTagProvider::new, ModItemTagProvider::new, ModRecipeProvider::new
        );
    }

    /**
     * Register a {@link ICapabilityProvider} for {@link Item Items}.
     * <p>
     * An example is {@link net.minecraft.world.level.block.ShulkerBoxBlock}.
     *
     * @param items entity types to register
     */
    @Deprecated(forRemoval = true)
    @SafeVarargs
    public static void registerItemContainer(ICapabilityProvider<ItemStack, Void, IItemHandler> capabilityProvider, Holder<? extends Item>... items) {
        NeoForgeCapabilityHelper.register((RegisterCapabilitiesEvent evt, Item item) -> {
            evt.registerItem(Capabilities.ItemHandler.ITEM, capabilityProvider, item);
        }, items);
    }
}
