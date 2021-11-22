package com.example.news.util

/**
 * Interface for mapping from entity to domain model and from domain model to entity
 * @param Entity type
 * @param DomainModel type
 */
interface EntityMapper <Entity, DomainModel>{

    fun mapFromEntity(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity
}
