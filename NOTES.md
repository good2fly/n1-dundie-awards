## Issues

### Major
* No transactions for the REST endpoints!! Add `@Transactional` to the endpoints, or manage transactions manually via Spring transaction manager. 
* The data source properties in `application.yaml` are ineffective, as the level `spring` is repeated. Remove one level of `spring`
* Also, the whole `spring.jpa.database-platform` section is bogus. No need to specify driver class or dialect (unless custom), it's all derived from the URL. 
* Controller directly calling repositories -> no service layer (if it meant to be a real app with layered architecture)
* Entities directly exposed in API, tightly coupling the API to the internal data model. This is a major antipattern!
* Application allows creating 'floating' employees, that is, employees with no organization, due to nullability of FK to `Organization`,
* No validation of the input (e.g. for non-null, valid length, content, etc.)
* No scrubbing of the input (i.e. remove or escape HTML tags) so it can lead to cross-site scripting attacks.
* `EmployeeController.createEmployee()` and `EmployeeController.updateEmployee()` ignore `dundieAwards` in the input
* `dundieAwards` should be non-nullable (either via `@Column` `nullable` attribute or using `int` primitive type for field)
* There's config for OpenAPI, but because Gradle is missing `springdoc-openapi-starter-webmvc-ui` there is no real OpenAPI support
* No logging, or even a logger
* No test coverage at all (except for the one test that simply wires up and runs the app)
* No error mapping (`@ControllerAdvice` + `@ExceptionHandler`), so exception stack traces are shown to the user
* General lack of `@Nonnull` usage. This is especially important when using mixed Java + Kotlin code as Kotlin 'understands' JSR‑305 annotations
* No way to tell 2 people with same first/last names apart - consider adding some unique identifier or combination of identifiers, like DoB, email, or phone.

### Minor
* Type mismatch between getter/setter for `dundieAwards`. Should all be `int` (and non-null in DB).
* We may want to either make the name of the organization unique, or add a 'key' field that is unique, to be able to tell 2 orgs apart (from the business perspective).
* `dundieAwards` is never initialized in `DataLoader` (or anywhere), so DB will have NULL values, probably not what we want
* `dundieAwards` is never actually used in the API, even though the app was supposed to be about these awards
* Do not call JPA `save()` on an already managed entity in `EmployeeController.updateEmployee()` - it'll unnecessarily call `merge()`. Let JPA do its write-behind.
* Do not use field injection, use ctor injection and make injected fields final
* Package name `dundie_awards` is non-conventional (underscore). Should be `dundieawards`
* DB table names should be singular, not plural
* `occuredAt` and `occured_at` are misspelled in `Activity.java`
* No API versioning (Spring 7 supports this out of the box)

### Nitpick
* Java 17 => Java 25
* Replace double negation: `!optionalEmployee.isPresent()` => `optionalEmployee.isEmpty()`
* `ActivityRepository` is not used in `EmployeeController` - we can remove it for now
* `@ManyToOne` on `Employee.organization` should have an explicitly configured join column name for consistency
* I'd prefer using functional style usage of `Optional`, e.g.
```
  return optionalEmployee.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
```

## Improvements
* Add logging (e.g. SLF4J + Logback)
* Add service layer to implement business logic, or even better, switch to hexagonal architecture (overkill for a toy project like this, though)
* Consume and produce DTOs instead of JPA entities in the API. Add adapters, ideally using some sort of adapter library, like MapStruct.
* Add caching (e.g. Redis + Spring `@Cacheable`, etc. annotations)
* Enable Spring Boot Actuator for health, 
* Add metrics (Micrometer)
* Add API for managing orgs, events, awards
* Consider using GraphQL instead of REST, giving the client more flexibility (more relevant for modern, single page or mobile apps)
* Separate Spring profiles/config for runtime, test, etc.
* Add Docker Compose file to start up the whole stack for local testing, especially after switching to PostgreSQL and adding Redis

## Miscellaneous
### Docker

* To build image using the Gradle plugin: `  ./gradlew bootBuildImage --imageName=dundie-awards:latest`
* To run (same port): `docker run --rm -p 3000:3000 dundie-awards:latest`
