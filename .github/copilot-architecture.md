## Architecture
- **MVVM** with `@Observable` view models
- **Protocol-based contracts** for all ViewModels (e.g. `ArticlesViewModelContract`)
- **Dependency Injection** via `InjectionProvider` using `register` / `byType`
- **Repository pattern** — `DefaultRepository` → `DefaultHttpClient` + `DefaultNewsDatabase`
- **NetworkBoundResource** via `ResourceProvider.networkBoundResource` returning `AsyncStream<Resource<T>>`

