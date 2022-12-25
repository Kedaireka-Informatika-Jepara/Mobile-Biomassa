package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.PanenNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PanenContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PanenNetwork>
): BaseContainer<PanenNetwork>()
