//
//  ResourceProvider.swift
//  news
//
//  Created by Mendez, Juan on 10/14/25.
//

import Foundation

/// A utility struct for providing resources from cache and/or network sources.
///
/// Use this struct to coordinate fetching data from a local cache and a remote network source,
/// automatically handling cache updates and error propagation. This is useful for repository/data layer patterns
/// where you want to abstract away the details of data synchronization.
struct ResourceProvider {
    /**
     Creates an asynchronous stream that emits resource states from cache and network sources.
    
     This method first loads data from the cache, then determines if a network fetch is needed.
     If so, it fetches from the network, saves the result to the cache, and emits updated data.
     Errors are propagated as Resource.ERROR.
    
     - Parameters:
     - loadFromCache: Closure to asynchronously load data from the local cache.
     - shouldFetchFromNetwork: Closure to determine if a network fetch is needed, given the cached data.
     - fetchFromNetwork: Closure to asynchronously fetch data from the network.
     - saveToCache: Closure to asynchronously save network data to the cache.
    
     - Returns: An AsyncStream emitting Resource states for the cached data type.
    
     - Note: Both generic types must conform to Codable and Equatable.
     */
    static func networkBoundResource<CachedType: Codable & Equatable, NetworkType: Codable & Equatable>(
        loadFromCache: @escaping () async -> CachedType,
        shouldFetchFromNetwork: @escaping (CachedType?) -> Bool,
        fetchFromNetwork: @escaping () async throws -> NetworkType,
        saveToCache: @escaping (NetworkType) async -> Void
    ) -> AsyncStream<Resource<CachedType>> {
        return AsyncStream { continuation in
            Task {
                let cachedValue = await loadFromCache()
                continuation.yield(Resource.LOADING(item: cachedValue))

                if shouldFetchFromNetwork(cachedValue) {
                    do {
                        let networkResult = try await fetchFromNetwork()
                        await saveToCache(networkResult)

                        continuation.yield(Resource.SUCCESS(item: await loadFromCache()))
                    } catch let error {
                        continuation.yield(Resource.ERROR(error: error))
                    }
                } else {
                    continuation.yield(Resource.SUCCESS(item: cachedValue))
                }

                continuation.finish()
            }
        }
    }
}
