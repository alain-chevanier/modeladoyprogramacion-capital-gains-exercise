AGENTS: build, test, and style quick reference

Build/lint/test
- Build (skip tests): ./mvnw clean package -DskipTests
- Format check (CI parity): ./mvnw git-code-format:validate-code-format
- Auto-format (pre-commit also installs via mvn verify): ./mvnw git-code-format:format-code
- Run all tests + coverage: ./mvnw clean test jacoco:report
- Open coverage: open target/site/jacoco/index.html
- Run a single test class: ./mvnw -Dtest=CalculateStockOperationsTaxesServiceTest test
- Run a single test method: ./mvnw -Dtest=CalculateStockOperationsTaxesServiceTest#calculateTaxes test
- Acceptance scenarios (PDF cases): ./mvnw -q -DskipTests package && ./run-tests
- Run app with sample: java -jar target/demo-0.0.1-SNAPSHOT.jar < examples/case1-input.txt

Code style guidelines
- Formatter: Google Java Format enforced by git-code-format (imports sorted; unused removed). Do not hand-format.
- Java 17, Spring Boot 3, JUnit 5, Mockito, AssertJ; prefer @Builder/@Jacksonized DTOs and constructor injection (@RequiredArgsConstructor).
- Money: always use BigDecimal (scale=2, RoundingMode.HALF_UP); prefer BigDecimalFactory.createMoneyAmount and custom (de)serializers.
- Packages: lowercase; Classes: UpperCamelCase; methods/fields: lowerCamelCase; constants: UPPER_SNAKE_CASE.
- JSON: use @JsonProperty for external names (e.g., unit-cost); keep adapters/ports/hexagonal boundaries.
- Error handling: validate inputs early; throw IllegalArgumentException for bad args and ArithmeticException for invalid math; avoid swallowing exceptions.
- Tests: unit tests in src/test/java; use mocks for boundaries; keep deterministic, small, self-contained.
- Config: application.yml with env overrides (NO_TAX_UPPER_LIMIT, TAX_PERCENTAGE); avoid reading from System directly in domain.
