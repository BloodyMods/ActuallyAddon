package prefab

import com.google.gson.Gson
import exnihilocreatio.registries.manager.IDefaultRecipeProvider

import java.util.ArrayList

abstract class BaseRegistryList<V>(gson: Gson, defaultRecipeProviders: List<IDefaultRecipeProvider>) : BaseRegistry<List<V>>(gson, ArrayList<V>(), null, defaultRecipeProviders) {

    override fun clearRegistry() {
        registry.clear()
    }

    fun register(value: V) {
        registry.add(value)
    }
}
