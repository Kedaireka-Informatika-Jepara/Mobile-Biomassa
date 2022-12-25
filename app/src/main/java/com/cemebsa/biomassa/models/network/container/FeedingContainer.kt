package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.FeedingNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedingContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<FeedingNetwork>
): BaseContainer<FeedingNetwork>()