package io.github.cotrin8672.mnsmoreclass.registry

interface IMnsRegistry<T> {
    fun add(value: T)

    fun getAll(): List<T>
}
