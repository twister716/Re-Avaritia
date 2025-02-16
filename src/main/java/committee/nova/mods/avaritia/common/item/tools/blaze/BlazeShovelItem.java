package committee.nova.mods.avaritia.common.item.tools.blaze;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import committee.nova.mods.avaritia.api.iface.ISwitchable;
import committee.nova.mods.avaritia.api.iface.ITooltip;
import committee.nova.mods.avaritia.api.iface.InitEnchantItem;
import committee.nova.mods.avaritia.init.registry.ModRarities;
import committee.nova.mods.avaritia.init.registry.ModToolTiers;
import committee.nova.mods.avaritia.init.registry.ModTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/2 20:00
 * Version: 1.0
 */
public class BlazeShovelItem extends ShovelItem implements ITooltip, ISwitchable, InitEnchantItem {
    private final String name;

    public BlazeShovelItem(String name) {
        super(ModToolTiers.BLAZE_SHOVEL, 0, 0f,
                new Properties()
                        .rarity(ModRarities.EPIC)
                        .stacksTo(1)
                        .fireResistant());

        this.name = name;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return false;
    }

    @Override
    public int getInitEnchantLevel(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.FIRE_ASPECT ? 10 : 0;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(ModTooltips.INIT_ENCHANT.args(Enchantments.FIRE_ASPECT.getFullname(10)).build());
        this.appendTooltip(stack, level, tooltipComponents, isAdvanced, name);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        var level = pContext.getLevel();
        var blockpos = pContext.getClickedPos();
        var blockstate = level.getBlockState(blockpos);
        if (blockstate.is(Blocks.GRAVEL)) {
            level.setBlockAndUpdate(blockpos, Blocks.NETHERRACK.defaultBlockState());
            return InteractionResult.SUCCESS;
        } else if (blockstate.is(Blocks.SAND)) {
            level.setBlockAndUpdate(blockpos, Blocks.SOUL_SAND.defaultBlockState());
            return InteractionResult.SUCCESS;
        } else if (blockstate.is(Blocks.DIRT)) {
            level.setBlockAndUpdate(blockpos, Blocks.SOUL_SOIL.defaultBlockState());
            return InteractionResult.SUCCESS;
        } else return super.useOn(pContext);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        if (slot == EquipmentSlot.MAINHAND) {
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", getTier().getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", getTier().getSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
}
