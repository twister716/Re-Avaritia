package committee.nova.mods.avaritia.common.item.tools.infinity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import committee.nova.mods.avaritia.common.entity.ImmortalItemEntity;
import committee.nova.mods.avaritia.init.registry.ModEntities;
import committee.nova.mods.avaritia.init.registry.ModRarities;
import committee.nova.mods.avaritia.init.registry.ModToolTiers;
import committee.nova.mods.avaritia.util.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/5/15 16:47
 * Version: 1.0
 */
public class InfinityHoeItem extends HoeItem {

    public InfinityHoeItem() {
        super(ModToolTiers.INFINITY_HOE, 0, 0f, (new Properties())
                .stacksTo(1)
                .fireResistant());

    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return false;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack pStack) {
        return ModRarities.COSMIC;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 0;
    }


    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        return Math.max(super.getDestroySpeed(stack, state), 6.0f);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()) {
            CompoundTag tags = stack.getOrCreateTag();
            tags.putBoolean("sow", !tags.getBoolean("sow"));
            player.swing(hand);
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) serverPlayer.sendSystemMessage(
                    Component.translatable(tags.getBoolean("sow") ? "tooltip.infinity_hoe.type_2" : "tooltip.infinity_hoe.type_1"
                    ), true);
            return InteractionResultHolder.success(stack);
        }
        if (!world.isClientSide && world instanceof ServerLevel serverLevel && stack.getOrCreateTag().getBoolean("sow")) {
            player.swing(hand);
            BlockPos blockPos = player.getOnPos();
            int rang = 7;
            int height = 2;
            ToolUtils.rangeHarvest(serverLevel, player, stack, blockPos, rang, height);
            ToolUtils.rangeBonemealable(serverLevel, blockPos, rang, height, 3);
            player.getCooldowns().addCooldown(stack.getItem(), 10);
            world.playSound(player, player.getOnPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 5.0f);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        var stack = context.getItemInHand();
        var world = context.getLevel();
        var blockpos = context.getClickedPos();
        var targetBlock = world.getBlockState(blockpos).getBlock();
        var player = context.getPlayer();
        var blockstate = Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7);
        int rang = 5;
        var minPos = blockpos.offset(-rang, 0, -rang);
        var maxPos = blockpos.offset(rang, 0, rang);
//        int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(context);
//        if (hook != 0) return hook > 0 ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        if (context.getClickedFace() != Direction.DOWN && world.isEmptyBlock(blockpos.above()) &&
                (targetBlock instanceof GrassBlock || targetBlock.equals(Blocks.DIRT) || targetBlock.equals(Blocks.COARSE_DIRT))) {
            if (player != null && !world.isClientSide) {
                if (player.isCrouching() && stack.getOrCreateTag().getBoolean("sow")) {
                    var boxMutable = BlockPos.betweenClosed(minPos, maxPos);
                    for (BlockPos pos : boxMutable) {
                        var state = world.getBlockState(pos);
                        var block = state.getBlock();

                        if (!world.isEmptyBlock(pos.above())) {
                            for (int i = 1; i <= 3; i++) {
                                harvest(world, pos.above(i));
                            }
                        }

                        if (world.isEmptyBlock(pos.above()) && (block instanceof GrassBlock || block.equals(Blocks.DIRT) || block.equals(
                                Blocks.COARSE_DIRT) || block instanceof FarmBlock)) {
                            world.setBlock(pos, blockstate, 11);
                        }
                        if (world.isEmptyBlock(pos) && !world.isEmptyBlock(pos.below())) {
                            world.setBlock(pos, blockstate, 11);
                        }
                        if (state.getMapColor(world, pos) == MapColor.WATER || state.getBlock() instanceof LiquidBlockContainer) {
                            world.setBlock(pos, blockstate, 11);
                        }
                    }

                    Iterable<BlockPos> inBoxMutable = BlockPos.betweenClosed(minPos, maxPos.offset(0, 3, 0));
                    Iterable<BlockPos> allInBoxMutable = BlockPos.betweenClosed(minPos.offset(-1, 0, -1), maxPos.offset(1, 4, 1));
                    for (BlockPos pos : allInBoxMutable) {
                        if (!hasBox(pos, inBoxMutable)) { //外壳坐标
                            var state = world.getBlockState(pos);
                            if (state.getMapColor(world, pos) == MapColor.WATER || state.getBlock() instanceof LiquidBlockContainer)
                                world.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
                        }
                    }
                } else world.setBlock(blockpos, blockstate, 11); //未潜行耕种一个方块
            }
            world.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private boolean hasBox(BlockPos pos, Iterable<BlockPos> box) {
        for (BlockPos pos1 : box) {
            if (pos1.getX() == pos.getX() && pos1.getY() == pos.getY() && pos1.getZ() == pos.getZ()) return true;
        }
        return false;
    }

    private void harvest(Level world, BlockPos pos) {
        var state = world.getBlockState(pos);
        var block = state.getBlock();
        if (world.isEmptyBlock(pos) || block instanceof CropBlock || block instanceof StemBlock
                || block instanceof CocoaBlock || block instanceof SweetBerryBushBlock) {
            return;
        }
        world.destroyBlock(pos, false);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity location, ItemStack stack) {
        return ImmortalItemEntity.create(ModEntities.IMMORTAL.get(), level, location.getX(), location.getY(), location.getZ(), stack);
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
