package prefab

import com.google.gson.Gson
import exnihilocreatio.api.registries.IRegistryMap
import exnihilocreatio.registries.manager.IDefaultRecipeProvider

import java.lang.reflect.Type
import java.util.HashMap

abstract class BaseRegistryMap<K, V>(gson: Gson, typeOfSource: Type, defaultRecipeProviders: List<IDefaultRecipeProvider>) : BaseRegistry<Map<K, V>>(gson, HashMap<K, V>(), typeOfSource, defaultRecipeProviders), IRegistryMap<K, V> {

    fun register(key: K, value: V) {
        registry.put(key, value)
    }

    override fun clearRegistry() {
        registry.clear()
    }

}
