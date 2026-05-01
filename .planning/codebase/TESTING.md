# Testing Patterns

## Server

- JUnit 5 + Spring Boot Test.
- AssertJ assertions.
- Test package mirrors main package.
- Current tests:
  - `server/src/test/java/com/stallmart/support/web/ResultTest.java`
  - `server/src/test/java/com/stallmart/order/OrderServiceTest.java`
  - `server/src/test/java/com/stallmart/web/ApiControllerTest.java`

Run:

```bash
cd server
./gradlew test
```

## App

The app currently has no unit test runner. Keep the existing lint baseline:

```bash
cd app
npm run lint
```
