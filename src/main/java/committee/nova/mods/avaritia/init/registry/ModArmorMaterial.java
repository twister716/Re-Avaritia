package committee.nova.mods.avaritia.init.registry;

import committee.nova.mods.avaritia.Static;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/21 15:13
 * Version: 1.0
 */
public class ModArmorMaterial {

    public static final ArmorMaterial infinite_armor = new SimpleArmorMaterial(new int[]{9999, 9999, 9999, 9999}, new int[]{6, 16, 12, 6}, 1000,
            SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ModItems.infinity_ingot.get()), Static.MOD_ID + ":" + "infinity_armor", 1.0f, 1.0f);

    public static class SimpleArmorMaterial implements ArmorMaterial {
        private final int[] durability;
        private final int[] damageReduction;
        private final int enchantability;
        private final SoundEvent soundEvent;
        private final LazyLoadedValue<Ingredient> repairMaterial;
        private final String textureName;
        private final float toughness;
        private final float knockbackResistance;

        public SimpleArmorMaterial(int[] durability, int[] damageReduction, int enchantability, SoundEvent soundEvent, Supplier<Ingredient> repairMaterial, String textureName, float toughness, float knockbackResistance) {
            this.durability = durability;
            this.damageReduction = damageReduction;
            this.enchantability = enchantability;
            this.soundEvent = soundEvent;
            this.repairMaterial = new LazyLoadedValue<>(repairMaterial);
            this.textureName = textureName;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
        }

        public static Builder builder() {
            return new Builder();
        }

        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return durability[slot.getIndex()];
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return damageReduction[slot.getIndex()];
        }

        @Override
        public int getEnchantmentValue() {
            return enchantability;
        }

        @Override
        public SoundEvent getEquipSound() {
            return soundEvent;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repairMaterial.get();
        }

        @Override
        public String getName() {
            return textureName;
        }

        @Override
        public float getToughness() {
            return toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return knockbackResistance;
        }

        public static class Builder {

            private final int[] durability = new int[4];
            private final int[] damageReduction = new int[4];
            private int enchantability;
            private SoundEvent soundEvent;
            private Supplier<Ingredient> repairMaterial;
            private String textureName;
            private float toughness;
            private float knockbackResistance;

            private Builder() {
            }

            public Builder durability(EquipmentSlot slot, int value) {
                durability[slot.getIndex()] = value;
                return this;
            }

            public Builder durabilityFactor(int factor) {
                for (int i = 0; i < 4; i++) {
                    durability[i] = ArmorMaterials.HEALTH_PER_SLOT[i] * factor;
                }
                return this;
            }

            public Builder durability(int[] durability) {
                System.arraycopy(durability, 0, this.durability, 0, 4);
                return this;
            }

            public Builder damageReduction(EquipmentSlot slot, int value) {
                damageReduction[slot.getIndex()] = value;
                return this;
            }

            public Builder damageReduction(int[] damageReduction) {
                System.arraycopy(damageReduction, 0, this.damageReduction, 0, 4);
                return this;
            }

            public Builder enchantability(int enchantability) {
                this.enchantability = enchantability;
                return this;
            }

            public Builder soundEvent(SoundEvent soundEvent) {
                this.soundEvent = soundEvent;
                return this;
            }

            public Builder repairMaterial(Supplier<Ingredient> repairMaterial) {
                this.repairMaterial = repairMaterial;
                return this;
            }

            public Builder textureName(String textureName) {
                this.textureName = textureName;
                return this;
            }

            public Builder toughness(float toughness) {
                this.toughness = toughness;
                return this;
            }

            public Builder knockbackResistance(float knockbackResistance) {
                this.knockbackResistance = knockbackResistance;
                return this;
            }

            public SimpleArmorMaterial build() {
                return new SimpleArmorMaterial(durability, damageReduction, enchantability, soundEvent, repairMaterial, textureName, toughness, knockbackResistance);
            }
        }

    }

}