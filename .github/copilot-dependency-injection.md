## Dependency Injection
- Register dependencies in `InjectionProvider`:
  ```swift
  InjectionProvider.register { DefaultRepository() as Repository }
  ```
- Inject with `@Inject`:
  ```swift
  @Inject private var repository: Repository
  ```

