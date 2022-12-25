package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.BiotaNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BiotaContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<BiotaNetwork>
): BaseContainer<BiotaNetwork>()