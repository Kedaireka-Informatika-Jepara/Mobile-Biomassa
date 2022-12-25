package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.PakanNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PakanContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<PakanNetwork>
): BaseContainer<PakanNetwork>()