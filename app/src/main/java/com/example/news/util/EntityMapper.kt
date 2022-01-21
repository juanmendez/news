package com.example.news.util

/**
 * Interface for mapping between entity model and domain model
 * @param EntityModel the entity model type
 * @param DomainModel the domain model type
 */
interface EntityMapper<EntityModel, DomainModel> {

    /**
     * Maps from entity model to domain model
     * @param entity the entity model
     * @return the domain model
     */
    fun toDomain(entityModel: EntityModel): DomainModel

    /**
     * Maps from domain model to entity model
     * @param domainModel the domain model
     * @return the entity model
     */
    fun toEntity(domainModel: DomainModel): EntityModel
}
