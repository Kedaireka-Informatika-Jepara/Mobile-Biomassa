package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.PengukuranNetwork
import com.cemebsa.biomassa.models.network.PerhitunganNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PerhitunganContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PerhitunganNetwork>
): BaseContainer<PerhitunganNetwork>()

