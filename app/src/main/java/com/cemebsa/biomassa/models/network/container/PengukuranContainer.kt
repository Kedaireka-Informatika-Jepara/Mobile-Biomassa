package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.PengukuranNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PengukuranContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PengukuranNetwork>
): BaseContainer<PengukuranNetwork>()

