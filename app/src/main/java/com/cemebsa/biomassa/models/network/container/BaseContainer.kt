package com.cemebsa.biomassa.models.network.container

abstract class BaseContainer<T>{
    abstract val status: Boolean
    abstract val message: String
    abstract val data: List<T>
}
