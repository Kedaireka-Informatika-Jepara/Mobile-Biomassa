package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.FeedingDetailNetwork
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedingDetailContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<FeedingDetailNetwork>
): BaseContainer<FeedingDetailNetwork>()