//
//  EntityMapper.swift
//  news
//
//  Created by Mendez, Juan on 9/16/25.
//

import Foundation

/**
 * Interface for mapping between entity model and domain model
 * @param EntityModel the entity model type
 * @param DomainModel the domain model type
 */
protocol EntityMapper where EntityModel: Sendable, DomainModel: Sendable {
    associatedtype EntityModel
    associatedtype DomainModel

    /// Maps from entity model to domain model
    func toDomain(_ entityModel: EntityModel) -> DomainModel

    /// Maps from domain model to entity model
    func toEntity(_ domainModel: DomainModel) -> EntityModel
}
