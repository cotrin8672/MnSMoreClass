package io.github.cotrin8672.mnsmoreclass.registry

class MnsRegistryDelegate<T> : IMnsRegistry<T> {
    private val values = mutableListOf<T>()

    override fun T.add() {
        values.add(this)
    }

    override fun getAll(): List<T> {
        return values.toList()
    }
}
