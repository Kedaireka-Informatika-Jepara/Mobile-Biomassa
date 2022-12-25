package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.KerambaNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KerambaContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<KerambaNetwork>,
): BaseContainer<KerambaNetwork>()