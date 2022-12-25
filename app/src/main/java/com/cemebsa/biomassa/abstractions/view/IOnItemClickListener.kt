package com.cemebsa.biomassa.abstractions.view

interface IOnItemClickListener<T> {
    fun onCLick()

    fun onClick(obj: T)

    fun onLongClick(obj: T): Boolean
}