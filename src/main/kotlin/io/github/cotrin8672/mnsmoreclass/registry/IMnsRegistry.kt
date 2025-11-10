package io.github.cotrin8672.mnsmoreclass.registry

interface IMnsRegistry<T> {
    fun T.add()

    fun getAll(): List<T>
}
