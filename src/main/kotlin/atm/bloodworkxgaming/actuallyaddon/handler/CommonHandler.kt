package atm.bloodworkxgaming.actuallyaddon.handler

import atm.bloodworkxgaming.bloodyLib.util.AbstractCommonHandler
import atm.bloodworkxgaming.actuallyaddon.ModConfig
import atm.bloodworkxgaming.actuallyaddon.ModItems
import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon
import atm.bloodworkxgaming.actuallyaddon.ModBlocks
import net.minecraft.block.BlockLog
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.DamageSource
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.ShapelessOreRecipe

object CommonHandler : AbstractCommonHandler(modItems = ModItems, modBlocks = ModBlocks)