package prefab

interface IRegistryRecipe {
    fun matches(input: Any): Boolean
    fun isDuplicate(other: IRegistryRecipe): Boolean
}