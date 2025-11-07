package io.github.cotrin8672.mnsmoreclass.datagen

import com.robertx22.library_of_exile.registry.IAutoGson
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class MnsDataProvider<T : IAutoGson<*>>(
    private val output: PackOutput,
    private val dataList: List<T>,
    private val kind: String,
    private val transform: (T) -> ResourceLocation,
) : DataProvider {
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        val pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, kind)

        val futures = dataList.map {
            val id = transform(it)
            val path = pathProvider.json(id)
            val json = it.toJson()

            DataProvider.saveStable(cachedOutput, json, path)
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun getName(): String {
        return "MnsDataProvider for $kind"
    }
}
