package prefab

import atm.bloodworkxgaming.bloodyLib.cache.SortingLinkedList
import com.google.gson.Gson
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.registries.IForgeRegistry
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type

abstract class BaseRegistry<RegType : IRegistryRecipe>(protected val gson: Gson, protected val typeOfSource: Type?, private val defaultRecipeProviders: List<IDefaultRecipeProvider<*>>) {
    protected var hasAlreadyBeenLoaded = false
    val recipeList = SortingLinkedList<ItemStack, RegType>({i, r -> r.matches(i)})


    fun saveJson(file: File) {
        try {
            FileWriter(file).use { fw ->
                // TODO remove null again
                if (typeOfSource != null) {
                    gson.toJson(registry, typeOfSource!!, fw)
                } else {
                    gson.toJson(registry, fw)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun loadJson(file: File) {
        if (hasAlreadyBeenLoaded) clearRegistry()

        if (file.exists() && ModConfig.misc.enableJSONLoading) {
            try {
                FileReader(file).use { fr ->
                    registerEntriesFromJSON(fr)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            registerDefaults()
            if (ModConfig.misc.enableJSONLoading) {
                saveJson(file)
            }
        }

        hasAlreadyBeenLoaded = true
    }

    fun mergeRegistry(reg: BaseRegistry<RegType>) {
        reg.recipeList.
    }

    protected abstract fun registerEntriesFromJSON(fr: FileReader)

    fun registerDefaults() {
        defaultRecipeProviders.forEach { recipeProvider -> recipeProvider.registerRecipeDefaults(this) }
    }

    abstract fun clearRegistry()
}

interface IDefaultRecipeProvider<in T : BaseRegistry<*>> {
    fun registerRecipeDefaults(registry: T)
}