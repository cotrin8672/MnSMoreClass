package io.github.cotrin8672.mnsmoreclass.registry

class MnsRegistryDelegate<T> : IMnsRegistry<T> {
    private val values = mutableListOf<T>()

    override fun add(value: T) {
        values.add(value)
    }

    override fun getAll(): List<T> {
        return values.toList()
    }
}
