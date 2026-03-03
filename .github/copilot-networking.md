# Networking

## Overview
- All requests go through `DefaultHttpClient.rawRequest`
- `HttpRouter` defines endpoints (see `DefaultHttpRouter.swift`)
- Responses are decoded using `DecoderFactory.iso8601Decoder` (ISO 8601 date strategy)
- Encoding uses `DecoderFactory.iso8601Encoder` (mirrors the decoder)

## HttpClient
- Protocol: `HttpClient`
- Implementation: `DefaultHttpClient`
- Raw requests return `HttpClientResponseRaw` (a tuple of `Data` + `HTTPURLResponse`)
- Typed requests return `HttpClientResponse<T: Decodable>`

## Error Handling
- All errors are normalized into `HttpError` before propagating:
  ```swift
  case invalidUrl
  case badResponse(status: Int, body: Data?, errors: [ErrorModel]?)
  case transport(message: String)
  case noHttpResponse
  case other(message: String)
  ```
- Never let raw `URLError` or `DecodingError` escape — always map to `HttpError`

## HttpClientResponseRaw
- Used in tests to mock raw HTTP responses
- Convenience initializer in `HttpClientResponseRaw+Common.swift` accepts any `Encodable`:
  ```swift
  sut.rawResponse = try HttpClientResponseRaw(
      item: PreviewConstants.articles[0],
      statusCode: 200,
      url: "https://newsapi.org",
      headerFields: [:]
  )
  ```

## Internet Connectivity
- Protocol: `InternetService`
- Implementation: `DefaultInternetService`
- Registered in `DefaultInjections` under `.internetService`
- Injected into `ArticlesViewModel` via its initializer
- Method: `func hasAccess() async -> Bool`
- Probes `https://www.apple.com/library/test/success.html` via `URLSession.shared.data(for:)`
- Timeout is set to `5.0` seconds
- Returns `false` on any error (timeout, no connection, etc.)

### Usage
```swift
// Injection
@Inject private var internetService: InternetService

// Guard before fetching
guard await internetService.hasAccess() else { return }
```

### Registration
```swift
dependencies[.internetService] = DefaultInternetService()
```

### Testing (MockInternetService)
- Create a hand-written mock conforming to `InternetService`
- Inject it into `ArticlesViewModel` via the initializer:
  ```swift
  let sut = ArticlesViewModel(
      repository: mockRepository,
      internetService: mockInternetService,
      pageSize: 10
  )
  ```

## Resource States
- All async data flows use `Resource<T>`:
  ```swift
  case LOADING(item: T?)
  case SUCCESS(item: T)
  case ERROR(error: Error)
  ```
- Use `ResourceProvider.networkBoundResource` to combine local DB + network fetch:
  ```swift
  ResourceProvider.networkBoundResource(
      loadFromCache: { ... },
      shouldFetchFromNetwork: { data in ... },
      fetchFromNetwork: { ... },
      saveToCache: { ... }
  )
  ```

## Query Parameters (getArticles)
- `q` — search query string
- `page` — current page number
- `pageSize` — number of results per page (default: `10`)
- `sortBy` — always `"publishedAt"`
- `language` — always `"en"`
- `apiKey` — injected via `DefaultRepository.apiKey`

## DecoderFactory
- `DecoderFactory.iso8601Decoder` — `JSONDecoder` with `.iso8601` date strategy
- `DecoderFactory.iso8601Encoder` — `JSONEncoder` with `.iso8601` date strategy
- Always use these for encoding/decoding API models to ensure date consistency
