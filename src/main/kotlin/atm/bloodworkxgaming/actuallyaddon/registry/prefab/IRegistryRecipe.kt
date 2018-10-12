package atm.bloodworkxgaming.actuallyaddon.registry.prefab

/**
 * Override equals for a duplicate check
 */
interface IRegistryRecipe {
    fun matches(input: Any): Boolean
}