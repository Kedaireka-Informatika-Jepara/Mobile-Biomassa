package com.cemebsa.biomassa.abstractions.mapper

interface IEntityMapper<Entity, Target> {
    fun mapFromEntity(entity: Entity): Target

    fun mapToEntity(target: Target): Entity
}