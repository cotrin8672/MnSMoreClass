package io.github.cotrin8672.mnsmoreclass.datagen

import com.robertx22.library_of_exile.registry.IAutoGson
import com.robertx22.library_of_exile.registry.serialization.ISerializable
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class MnsGsonDataProvider<T : IAutoGson<*>>(
    private val output: PackOutput,
    private val dataList: List<T>,
    private val kind: String,
    private val transform: (T) -> ResourceLocation,
) : DataProvider {
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        val pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, kind)

        val futures = dataList.map { entry ->
            val id = transform(entry)
            val path = pathProvider.json(id)
            val json = entry.toJson()

            DataProvider.saveStable(cachedOutput, json, path)
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun getName(): String {
        return "MnsGsonDataProvider for $kind"
    }
}

class MnsSerializableDataProvider<T : ISerializable<*>>(
    private val output: PackOutput,
    private val dataList: List<T>,
    private val kind: String,
    private val transform: (T) -> ResourceLocation,
) : DataProvider {
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        val pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, kind)

        val futures = dataList.map { entry ->
            val id = transform(entry)
            val path = pathProvider.json(id)
            val json = entry.toJson()

            DataProvider.saveStable(cachedOutput, json, path)
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun getName(): String {
        return "MnsSerializableDataProvider for $kind"
    }
}
