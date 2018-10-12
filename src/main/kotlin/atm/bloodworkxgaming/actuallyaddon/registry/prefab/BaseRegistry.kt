package atm.bloodworkxgaming.actuallyaddon.registry.prefab

import atm.bloodworkxgaming.bloodyLib.cache.SortingLinkedList
import de.ellpeck.actuallyadditions.mod.ActuallyAdditions
import net.minecraft.item.ItemStack

abstract class BaseRegistry<RegType : IRegistryRecipe> {
    protected val recipeList = SortingLinkedList<ItemStack, RegType>({ i, r -> r.matches(i) })
    fun clearRegistry() = recipeList.clear()

    fun register(value: RegType) {
        if (recipeList.contains(value)) {
            ActuallyAdditions.LOGGER.info("Recipe $value exists already in a similar way")
            return
        }

        recipeList.add(value)
    }
}

interface IDefaultRecipeProvider<in T : BaseRegistry<*>> {
    fun registerRecipeDefaults(registry: T)
}