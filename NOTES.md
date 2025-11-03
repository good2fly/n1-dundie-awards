## Issues

### Major
* ✅[^1] No transactions for the REST endpoints!! Add `@Transactional` to the endpoints, or manage transactions manually via Spring transaction manager.
* ✅ The data source properties in `application.yaml` are ineffective, as the level `spring` is repeated. Remove one level of `spring`
* ✅ Also, the whole `spring.jpa.database-platform` section is bogus. No need to specify driver class or dialect (unless custom), it's all derived from the URL. 
* ✅️ Controller directly calling repositories -> no service layer (if it meant to be a real app with layered architecture)
* ⚠️ Entities directly exposed in the API, tightly coupling the API to the internal data model. This is a major antipattern!
  Not only that, but it leads to weird situations where we require an organization sub-object to be present when cratiny an employee when we only need an org ID.
* ⚠️ Application allows creating 'floating' employees, that is, employees with no organization, due to nullability of FK to `Organization`,
* ⚠️ No validation of the input (e.g. for non-null, valid length, content, etc.)
* ⚠️ No scrubbing of the input (i.e. remove or escape HTML tags) so it can lead to cross-site scripting attacks.
* ✅ `dundieAwards` should be non-nullable (either via `@Column` `nullable` attribute or using `int` primitive type for field)
* ✅ There's config for OpenAPI, but because Gradle is missing `springdoc-openapi-starter-webmvc-ui` there is no real OpenAPI support
* ✅ No logging, or even a logger
* ⚠️ No test coverage at all (except for the one test that simply wires up and runs the app)
* ⚠️ No error mapping (`@ControllerAdvice` + `@ExceptionHandler`), so exception stack traces are shown to the user
* ⚠️ General lack of `@Nonnull` usage. This is especially important when using mixed Java + Kotlin code as Kotlin 'understands' JSR‑305 annotations
* ⚠️ No way to tell 2 people with same first/last names apart - consider adding some unique identifier or combination of identifiers, like DoB, email, or phone.
* ⚠️ In a real app, initial (and subsequent updates) schema s not typically generated directly by JPA. Instead, use some migration tool (e.g. Flyway) to manage schema evolution.
* ⚠️ I would personally not use JPA, unless most of the team is very familiar with the various (mostly performance) pitfalls around JPA/Hibernate. 
  It's also not very conducive for Domain Driven Design, as it encourages YOLO implementation and anemic object model. Prefer Spring Data JDBC.

### Medium/Minor
* ✅ Type mismatch between getter/setter for `dundieAwards`. Should all be `int` (and non-null in DB).
* ⚠️ `EmployeeController.createEmployee()` and `EmployeeController.updateEmployee()` ignore `dundieAwards` in the input.
  This may be fine if we want to handle awards via a separate endpoint, but then it should not be in the input either.
* ⚠️ `EmployeeController.updateEmployee` really only updates the first/last names and ignores organization and dundie awards. If that's the case it should be called `changeEmployeeName` and perhaps a different REST endpoint.
* ✅ The `id` field is primitive `long` in the entities, instead of `Long`. While this is not a fatal mistake, it's unconventional and confusing (Hibernate treats id=0 as "unsaved" entity, but 0 is also a valid DB ID value).
* ⚠️ We may want to either make the name of the organization unique, or add a 'key' field that is unique, to be able to tell 2 orgs apart (from the business perspective).
* ✅ `dundieAwards` is never initialized in `DataLoader` (or anywhere), so DB will have NULL values, probably not what we want
* ⚠️ `dundieAwards` is never actually used in the API, even though the app was supposed to be about these awards
* ✅ Do not call JPA `save()` on an already managed entity in `EmployeeController.updateEmployee()` - this is bad practice, and it'll unnecessarily call `merge()`. Let JPA do its write-behind.
* ✅ Do not use field injection, use ctor injection and make injected fields final
* ✅ No setter for fields in `Activity` 
* ⚠️ Package name `dundie_awards` is non-conventional (underscore). Should be `dundieawards`
* ⚠️ DB table names should be singular, not plural
* ✅ `occuredAt` and `occured_at` are misspelled in `Activity.java`
* ⚠️ No API versioning (Spring 7 supports this out of the box)
* ✅ `EmployeeController.getAllEmployees` does not follow the pattern of the other endpoints returning `ResponseEntity`.
* ✅ `EmployeeController.createEmployee` does not follow the pattern of the other endpoints returning `ResponseEntity`. Also, it is customary to return 201 - Created status here.
* ✅ No `toString` implementation in the entities, so can't meaningfully log them
* ✅ Replace `@Controller` with `@RestController`, remove `@ResponseBody`
* ⚠️ `Activity` uses `LocalDateTime` for occurred-at. This may or may not be what we want, but if we want absolute time, we should use `OffsetDateTime` or `Instant`.
* ⚠️ Repository scanning should be restricted to only the `repository` directory, otherwise app startup time will suffer 
* ⚠️ Initial data should not normally be provided within the application code. Use some init SQL script(s), possibly via Flyway

### Nitpick
* ✅  Replace double negation: `!optionalEmployee.isPresent()` => `optionalEmployee.isEmpty()`
* ✅ `ActivityRepository` is not used in `EmployeeController` - we can remove it for now
* ⚠️ `@ManyToOne` on `Employee.organization` should have an explicitly configured join column name for consistency
* ⚠️ Returning a response body from `DELETE` is really not necessary. It's customary to just return 204 - No Content
* ⚠️ I'd prefer using functional style usage of `Optional`, e.g.
```
  return optionalEmployee.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
```

## Improvements
* Java 17 => Java 25
* ✅ Add logging (e.g. SLF4J + Logback)
* Add service layer to implement business logic, or even better, switch to hexagonal architecture (overkill for a toy project like this, though)
* Consume and produce DTOs instead of JPA entities in the API. Add adapters, ideally using some sort of adapter library, like MapStruct.
* ✅ Add audit fields (e.g. createdAt, createdBy, updatedAt, updatedBy) to the entities.
* Add entity versioning (e.g. using `@Version`) to entities in order to support optimistic locking.
* Add caching (e.g. Redis + Spring `@Cacheable`, etc. annotations)
* Consider adding missing indexes on FK columns (e.g. `organization_id` in `employees`).
  These will be most likely needed for reverse joins (i.e. join organizations to employees) at some point and engineers probably won't remember doing them then,
  leading to very poor DB performance.
* When returning object (DTOs or entities) in read-only endpoints, it's better to use projection queries to avoid various Hibernate related issues,
  like N+1 select, or accidentally loading a bunch of related objects that are not actually needed in the result.
* Enable Spring Boot Actuator for health checks, etc.
* Add authentication and authorization (Spring Security)
* Add metrics (Micrometer)
* Add API for managing orgs, events, awards
* Consider using GraphQL instead of REST, giving the client more flexibility (more relevant for modern, single page or mobile apps)
* Separate Spring profiles/config for runtime, test, etc.
* Organize by use cases rather than technical layers to make code easier to understand and change.
* Add Docker Compose file to start up the whole stack for local testing, especially after switching to PostgreSQL and adding Redis

## Miscellaneous Notes
### Docker

* To build image using the Gradle plugin: `  ./gradlew bootBuildImage --imageName=dundie-awards:latest`
* To run (same port): `docker run --rm -p 3000:3000 dundie-awards:latest`

[^1]: ✅ means fix is already implemented, ⚠️means not yet.
