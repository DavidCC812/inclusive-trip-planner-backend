# Inclusive Trip Planner – Test Plan

## 1. Introduction

The purpose of this document is to formally define the testing approach used to validate the Inclusive Trip Planner application. This mobile-first project aims to empower users with accessibility needs to confidently plan and explore curated travel itineraries. It features a native Android frontend built with Jetpack Compose and a Java-based backend developed using Spring Boot.

This test plan outlines the strategy, tools, test cases, and environments used to assess the quality and reliability of the system. It focuses on verifying core functionalities such as authentication, accessibility feature selection, itinerary browsing, user reviews, and data persistence.

Testing is carried out at multiple levels: unit tests to verify individual components (e.g., ViewModels and service classes), integration tests to validate the backend API and database behavior, and manual testing to simulate end-user interaction flows.

This document is intended for all project stakeholders, including:
- The **project owner and developer** (David Cuahonte Cuevas)
- **Academic evaluators**, responsible for grading technical quality and compliance
- Future **contributors or testers**, who may build upon this foundation

Ultimately, the goal of this test plan is to ensure that the Inclusive Trip Planner MVP meets its functional requirements and provides a stable and inclusive experience for its target users.

## 2. Test Objectives

The primary objective of this test plan is to ensure that the Inclusive Trip Planner application functions correctly, reliably, and securely across its key components. Testing focuses on validating both frontend and backend logic in alignment with the core user stories and functional requirements defined for the MVP.

### Functional Objectives

- Verify that users can complete the full signup process, including:
  - Entering personal details (name, email/phone, password)
  - Selecting accessibility preferences
  - Choosing countries and destinations
- Ensure successful authentication via email/password and social login (Google/Facebook)
- Confirm that users can:
  - Browse available itineraries
  - View itinerary details and steps
  - Save and remove itineraries
  - Submit reviews with a rating and comment
- Validate that settings and preferences are stored and reflected in the UI

### Security Objectives

- Validate JWT token generation, verification, and expiry
- Ensure that protected endpoints are not accessible without valid tokens
- Confirm that OAuth flows properly validate identity and prevent unauthorized access

### Integration Objectives

- Ensure consistent data flow between frontend and backend via Retrofit and REST APIs
- Confirm synchronization of user state, itinerary data, and reviews across client and server
- Validate data persistence using a relational database (PostgreSQL in production, H2 in test)

### MVP Acceptance Criteria

The application will be considered functionally complete and test-passing if:
- All implemented ViewModels pass unit tests for normal and failure states
- Backend endpoints behave correctly under integration tests (CRUD and security)
- All critical user flows can be completed without error during manual testing
- Data integrity and consistency are maintained across all tested scenarios

## 3. Scope

This section defines the boundaries of the testing effort for the Inclusive Trip Planner MVP. It identifies the features and components that are included in the test campaign, as well as those explicitly excluded due to project constraints or deferred implementation.

### Included in Scope

The following features and modules are covered by unit tests, integration tests, and/or manual validation:

- **Authentication and Account Management**
  - Email/password login
  - Google and Facebook login (via Firebase)
  - Token-based session management

- **User Signup Flow**
  - Name, email, phone, and password input
  - Selection of accessibility features
  - Selection of countries and destinations

- **Itinerary Exploration**
  - Viewing available itineraries
  - Viewing itinerary details and steps
  - Filtering and searching itineraries (frontend-side logic)
  - Viewing accessibility metadata per itinerary

- **Saved Itineraries**
  - Saving and removing itineraries
  - Setting an itinerary as “Next Plan”
  - Displaying saved itineraries on the home screen

- **Reviews**
  - Submitting reviews with ratings and comments
  - Fetching and displaying reviews per itinerary

- **Settings**
  - Fetching global settings
  - Linking user-specific settings and preferences

- **Backend API and Data Layer**
  - Entity CRUD operations (User, Itinerary, Review, etc.)
  - Security: JWT verification and protected endpoints
  - Database persistence (PostgreSQL in production, H2 in tests)

### Excluded from Scope

The following features are currently out of scope for this test plan:

- **OTP verification and SMS logic** (placeholder screen exists, but no backend integration)
- **Offline or caching mechanisms**
- **Dynamic itinerary filtering based on accessibility features** (planned feature, not implemented)
- **Performance, scalability, or load testing**
- **Automated end-to-end UI tests (e.g., Espresso or UI Automator)**
- **Admin dashboard or itinerary creation interface**
- **Multilingual support and localization testing**

These exclusions are either due to the defined MVP scope or because they are part of future planned iterations.

## 4. Test Strategy

The testing strategy for the Inclusive Trip Planner follows a layered approach that combines unit testing, integration testing, and manual validation. Each layer targets specific components of the system to ensure reliability, correctness, and security from both technical and user-facing perspectives.

### Testing Levels

| Level                | Description                                                                 |
|----------------------|-----------------------------------------------------------------------------|
| Unit Testing          | Validates individual components in isolation (e.g., ViewModels, services). |
| Integration Testing   | Verifies interaction between components, particularly backend API and database logic. |
| Manual Testing        | Used to validate complete user workflows and UI feedback on the Android emulator. |

### Testing Types

| Type                 | Purpose                                                                    |
|----------------------|----------------------------------------------------------------------------|
| Functional Testing    | Ensures features behave according to the specifications.                   |
| Security Testing      | Validates authentication, JWT handling, and endpoint protection.           |
| Regression Testing    | Confirms that new changes do not break previously working functionality.   |

### Frontend Testing Approach

- **Frameworks**: JUnit5, MockK, Kotlin Coroutines Test, Turbine
- **Focus**:
  - ViewModel logic and state handling
  - API call simulation using mocks
  - Validation of loading/error/success flows
  - Stream testing via `Flow` and `StateFlow`

### Backend Testing Approach

- **Frameworks**: SpringBootTest, JUnit5, MockMvc, H2 database
- **Focus**:
  - Full controller → service → repository pipeline
  - Authenticated requests using JWT tokens
  - Test isolation using `@BeforeEach` setup and `@ActiveProfiles("test")`
  - CRUD operations and custom business logic
  - Validation of error cases, security constraints, and data persistence

### Manual Testing

- Run periodically using Android Emulator (API 33)
- Emulates complete flows such as:
  - Signup and login
  - Feature selection
  - Viewing and saving itineraries
  - Submitting a review
  - Modifying user settings
- Captures UX anomalies not covered by automated tests

### Tooling and Automation

| Tool             | Usage                                      |
|------------------|---------------------------------------------|
| JUnit 5          | Unit and integration testing framework      |
| MockK            | Mocking dependencies in ViewModel tests     |
| Turbine          | Testing Kotlin Flows                        |
| MockMvc          | Simulating HTTP requests in backend tests   |
| H2               | In-memory database for backend integration  |
| Liquibase (test) | Disabled in test profile for controlled setup |
| TokenManager     | Custom JWT management in frontend tests     |

This strategy ensures that all critical layers of the application are tested with a combination of automated and manual techniques.

## 5. Test Environment

This section describes the technical environment in which the Inclusive Trip Planner application is developed and tested. The setup ensures consistency between development, test, and production contexts where possible, while using isolated configurations for safe and repeatable testing.

### Frontend Environment

| Component             | Description                                      |
|-----------------------|--------------------------------------------------|
| Platform              | Android (target SDK: 33)                         |
| Framework             | Jetpack Compose                                 |
| Language              | Kotlin                                           |
| Emulator              | Pixel 5, Android 13 (API 33)                     |
| Unit Test Tools       | JUnit5, MockK, Turbine, Coroutine Test           |
| Dependency Injection  | Manual (constructor-based)                      |
| Retrofit API Layer    | Used for mocking API calls in unit tests         |

All frontend tests are executed using the Android Studio test runner, with emulators used to verify behavior across realistic flows and devices.

### Backend Environment

| Component             | Description                                      |
|-----------------------|--------------------------------------------------|
| Framework             | Spring Boot 3.x                                  |
| Language              | Java 17                                          |
| Database (test)       | H2 (in-memory)                                   |
| Database (prod)       | PostgreSQL                                       |
| ORM                   | Spring Data JPA                                  |
| Security              | JWT-based stateless authentication               |
| Integration Test Tools| JUnit5, MockMvc, SpringBootTest, Testcontainers (if needed) |

A separate `test` profile is used in integration tests to isolate data and configuration. Liquibase changelogs are disabled in the test environment to allow custom setup via `@BeforeEach` in test classes.

### CI/CD and Execution

| Aspect            | Status               |
|-------------------|----------------------|
| Continuous Testing| Manual (via IDE and CLI) |
| GitHub Actions    | Not yet configured   |
| Test Frequency    | On feature branch merge, or before manual validation |

This environment ensures consistent and repeatable testing across all key modules and provides full traceability of test outcomes during development and quality assurance stages.

## 6. Test Data Management

Reliable and reproducible testing requires well-defined strategies for initializing and managing test data. The Inclusive Trip Planner project uses both automated and manual data setup techniques, tailored to each layer of the system.

### Backend Test Data Strategy

Backend integration tests use an in-memory H2 database to isolate data from production and avoid side effects. Each test class includes a controlled setup that initializes only the entities needed for that test.

- **Isolation**: Tests are independent and reset state between runs using `@BeforeEach`
- **UUIDs**: All test entities (e.g., users, destinations) use randomly or explicitly generated UUIDs to avoid collisions
- **Token Generation**: JWT tokens are programmatically created in test setup methods to simulate authenticated access
- **Relationships**: Linked entities (e.g., users with settings, itineraries with reviews) are created in sequence within each test
- **Liquibase**: Disabled during tests to allow for programmatic setup without interfering with controlled test schemas

This approach provides full control over the data lifecycle during testing and avoids unintended interactions between tests.

### Frontend Test Data Strategy

Unit tests for ViewModels use mock data to simulate API responses and user input. No network or database is used during these tests.

- **MockK** is used to define expected outputs for mocked API calls
- **Turbine** is used to observe `Flow` emissions from ViewModels
- **UUIDs** are used for all test objects to simulate realistic entity identifiers
- **State verification** includes:
  - Empty state
  - Loading state
  - Success state with mock data
  - Error state triggered by mock exceptions

Each test defines exactly which values are expected to be emitted or stored in the ViewModel state after calling a method, ensuring full visibility into behavior under normal and failing conditions.

### Summary

| Layer     | Strategy                         | Tooling Used                          |
|-----------|----------------------------------|----------------------------------------|
| Backend   | In-memory DB, manual setup       | JUnit5, H2, MockMvc, SpringBootTest    |
| Frontend  | Mocked API responses, fake UUIDs | JUnit5, MockK, Coroutine Test, Turbine |

The overall goal is to ensure that test outcomes are repeatable, isolated, and aligned with real-world usage scenarios.

## 7. Test Case Structure

All test cases in the Inclusive Trip Planner project follow a consistent structure to ensure clarity, repeatability, and verifiability. This section defines the standard format used across both unit and integration tests, along with representative examples from the frontend and backend.

### Test Case Format

Each test is documented and structured according to the following fields:

- **Test ID**: A unique identifier (e.g., `VM-001`, `API-005`)
- **Title**: A short description of what the test validates
- **Component**: The part of the system under test (e.g., ViewModel, Controller)
- **Preconditions**: Required setup or state before execution
- **Test Steps**: Ordered list of actions or method calls
- **Expected Result**: What the system should return or display
- **Result Status**: Pass or Fail (recorded during execution)

This structure is implicit in code for automated tests, but is followed consistently to allow traceability between requirements and test coverage.

---

### Example: Frontend Unit Test Case

| Field            | Example                                                  |
|------------------|----------------------------------------------------------|
| Test ID          | `VM-003`                                                 |
| Title            | `fetchCountries should update state on API success`      |
| Component        | `CountryViewModel`                                       |
| Preconditions    | API mock returns two countries                           |
| Test Steps       | 1. Call `fetchCountries()`<br>2. Advance coroutine        |
| Expected Result  | ViewModel updates state with country list, `isLoading = false` |
| Result Status    | Pass (assertions met)                                    |

---

### Example: Backend Integration Test Case

| Field            | Example                                                   |
|------------------|-----------------------------------------------------------|
| Test ID          | `API-010`                                                 |
| Title            | `POST /api/reviews - should return 201 Created`           |
| Component        | `ReviewController`                                        |
| Preconditions    | Valid JWT, user and itinerary exist                       |
| Test Steps       | 1. Authenticate user<br>2. POST request with rating/comment |
| Expected Result  | HTTP 201 response with saved review in response body      |
| Result Status    | Pass                                                      |

---

### Assertion Types

- **Value assertions**: Checking exact values returned in state or response
- **State assertions**: Verifying loading/error/success flags
- **Structural assertions**: Confirming object size, non-null fields
- **Security assertions**: Ensuring access is denied without token
- **Error handling assertions**: Simulating failure and checking fallbacks

This consistent structure allows efficient review and validation of test coverage across all layers of the application.

## 8. Frontend Test Coverage

This section details the unit test coverage implemented for each ViewModel in the Inclusive Trip Planner application. The tests validate that core business logic, state transitions, and API interactions behave as expected under both normal and exceptional conditions.

All tests are implemented using **JUnit 5**, **MockK**, and **Kotlin Coroutines Test**, with **Turbine** used to observe asynchronous `Flow` emissions where applicable.

### ViewModels and Tested Scenarios

| ViewModel                    | Tested Scenarios                                                                 |
|-----------------------------|----------------------------------------------------------------------------------|
| **AccessibilityFeatureViewModel** | - Fetch accessibility features (success and failure)<br>- Fetch user features by ID<br>- Map selected labels to feature names |
| **CountryViewModel**        | - Load all countries (success and error)<br>- Confirm fallback on API failure   |
| **ItineraryAccessibilityViewModel** | - Ensure default list size and static data is correct                   |
| **ItineraryStepViewModel**  | - Load steps for itinerary<br>- Handle backend error<br>- Set appropriate error state |
| **ItineraryViewModel**      | - Fetch all itineraries (success and failure)<br>- Retrieve itinerary by ID (match and not found)<br>- Maintain state on error |
| **ReviewViewModel**         | - Fetch all reviews<br>- Filter reviews by itinerary ID<br>- Submit a review (success and error cases) |
| **SavedItinerariesViewModel** | - Load user’s saved itineraries<br>- Save and remove itineraries<br>- Set “next plan” from saved list |
| **SettingsViewModel**       | - Load global and user-specific settings<br>- Update existing settings (PUT)<br>- Create new user setting (POST)<br>- Handle API errors |
| **SignUpViewModel**         | - Update user input fields (name, email, password, etc.)<br>- Submit accessibility features<br>- Submit selected countries and destinations<br>- Execute full signup flow with mocked backend |
| **UserViewModel**           | - Create user via API<br>- Fetch user by ID and email<br>- Execute login and store token<br>- Handle invalid credentials gracefully |

### General Test Behaviors

- **State validation**: Each ViewModel’s `StateFlow` is verified for correct values before and after API interaction.
- **Loading indicators**: Tests assert correct toggling of loading state (`isLoading`) in async flows.
- **Error handling**: All API exceptions are mocked and tested to ensure fallback logic is triggered.
- **Callback behavior**: For actions like review posting or login, success/error callbacks are validated.

### Coverage Summary

| Category            | Value                     |
|---------------------|---------------------------|
| Total ViewModels    | 10                        |
| Total Unit Tests    | 40+                       |
| API Interaction     | Mocked with full coverage |
| Coroutines          | Handled via `runTest` and `advanceUntilIdle()` |
| Flows/Streams       | Tested using `Turbine`    |

The frontend testing layer provides full coverage of the app’s reactive state logic and validates how ViewModels manage asynchronous operations in response to user input and backend API responses.

## 9. Backend Test Coverage

Backend testing is performed through integration tests that simulate real HTTP requests and validate end-to-end behavior across the controller, service, and repository layers. All tests are executed using Spring Boot’s testing framework, with a focus on functional correctness, security, and database integrity.

### Testing Tools and Setup

- **Framework**: Spring Boot 3.x with JUnit 5
- **Database**: H2 (in-memory, isolated per test class)
- **Security**: JWT authentication simulated with real tokens
- **Execution**: `@SpringBootTest` with `@AutoConfigureMockMvc` and `@ActiveProfiles("test")`
- **Isolation**: Custom test data is set up using `@BeforeEach` without relying on Liquibase

### Entities and Test Classes

| Entity / Module                   | Test Class                                          | Tested Features                                  |
|----------------------------------|-----------------------------------------------------|--------------------------------------------------|
| `User`                           | `UserServiceTest`                                   | Create user, fetch by ID/email, login scenarios |
| `Auth`                           | `AuthServiceTest`, `JwtServiceTest`                 | JWT creation and validation, Firebase login      |
| `Setting`                        | `SettingServiceTest`                                | Default settings, global settings               |
| `UserSetting`                    | `UserSettingServiceTest`                            | Fetch, update, and create per-user settings     |
| `AccessibilityFeature`          | `AccessibilityFeatureServiceTest`                   | List, fetch, and link to users or itineraries   |
| `UserAccessibilityFeature`      | `UserAccessibilityFeatureServiceTest`               | Unique linking per user and feature             |
| `Country`                        | `CountryServiceTest`                                | Country creation, availability, and filtering   |
| `UserCountryAccess`             | `UserCountryAccessServiceTest`                      | Per-user access rules, duplicate checks         |
| `Destination`                   | `DestinationServiceTest`                            | Destination filtering and country linkage       |
| `UserSelectedDestination`       | `UserSelectedDestinationServiceTest`                | Bookmarking per user                            |
| `Itinerary`                     | `ItineraryServiceTest`, `ItinerarySecurityIntegrationTest` | CRUD operations, JWT protection      |
| `ItineraryStep`                 | `ItineraryStepServiceTest`                          | Step ordering, location info, itinerary links   |
| `ItineraryAccessibility`        | `ItineraryAccessibilityServiceTest`                 | Map features to itineraries                     |
| `SavedItinerary`                | `SavedItineraryServiceTest`                         | Save/remove logic, fetch by user ID             |
| `Review`                        | `ReviewServiceTest`                                 | Submit and fetch reviews per itinerary          |

### Covered Scenarios

- **Authentication**
  - JWT generation, decoding, and invalid token rejection
  - Firebase OAuth login using mocked payloads
  - Authenticated access enforced for protected routes

- **CRUD Logic**
  - Create, retrieve, delete for all primary entities
  - Relationship management between users, features, itineraries, and reviews
  - Uniqueness constraints tested (e.g., one review per user per itinerary)

- **Validation and Errors**
  - Missing or invalid input returns correct HTTP status (`400`, `401`, `403`)
  - Duplicate entries trigger error responses (e.g., `409 Conflict`)
  - Unauthenticated requests are blocked

- **Security**
  - Endpoints verified for correct role access
  - Authorization header required for restricted actions
  - JWT parsing failures handled gracefully

### Summary

| Metric                     | Value                    |
|----------------------------|--------------------------|
| Total Test Classes         | 16                       |
| Secured Endpoints Covered  | Yes                      |
| Database Persistence Tested| Yes (H2, with isolation) |
| Token Handling             | Fully validated          |
| Known Failures             | None at time of writing  |

These integration tests provide strong guarantees for backend reliability, covering all major entities, relationships, and secure flows in the application.

## 10. Limitations and Constraints

While the Inclusive Trip Planner MVP is backed by solid unit and integration test coverage, a few limitations remain due to the scope and focus of this initial version. These limitations are recognized and planned for future iterations as the project evolves toward a production-ready release.

### Technical Limitations

- **End-to-End (E2E) Tests Not Yet Implemented**  
  Full UI automation (e.g., with Espresso or UI Automator) is not yet in place. User flows have been tested manually through the Android emulator.

- **No Load or Performance Testing**  
  The current test suite does not include stress or concurrency tests. As the application scales or handles larger datasets, such scenarios will require dedicated evaluation.

- **Accessibility Automation Not Included**  
  Although accessibility is central to the app’s mission, automated WCAG validation tools (such as axe-core or Google Accessibility Scanner) are not yet integrated into the testing pipeline.

- **Error Handling Coverage Focuses on Typical Failures**  
  Most failure paths are tested using common exception scenarios. Advanced handling such as network timeouts, slow responses, or retry mechanisms is outside the current test scope.

- **Token Expiry and Refresh Not Covered**  
  JWT authentication is fully tested, but scenarios involving token expiration and automatic refresh are deferred for a future implementation phase.

- **Placeholder Features Not Covered by Tests**  
  Some features, such as OTP verification screens, are included in the UI as placeholders and have not been wired to backend logic or tested at this time.

### Scope Considerations

These constraints reflect conscious decisions made during MVP development to prioritize core functionality, stability, and testability. They do not affect critical user flows but may impact resilience under uncommon usage patterns or long-term scaling.

Future development cycles will include improvements in test depth, automation coverage, and performance profiling to support a production-grade release.

## 11. Test Results Summary

This section summarizes the outcome of the test efforts for the Inclusive Trip Planner MVP. It presents the results of automated unit and integration tests, as well as key observations from manual testing.

### Unit Testing Results

All ViewModels have been covered with unit tests that verify state transitions, API response handling, and error conditions. Each test was executed using isolated mocks and coroutine-based test dispatchers to simulate asynchronous behavior.

| Layer           | Component             | Status  | Notes                                |
|-----------------|------------------------|---------|--------------------------------------|
| Frontend        | ViewModel logic        | Passed  | All core states tested               |
| Frontend        | API interaction mocks  | Passed  | All key endpoints simulated          |
| Frontend        | Flow state handling    | Passed  | Verified with Turbine and assertions |

Total unit tests executed: **47+**  
Test failures: **0**

### Integration Testing Results

Backend integration tests were performed across all key entities using Spring Boot, MockMvc, and an in-memory H2 database. Each test was isolated with controlled setup and executed within a real Spring context.

| Layer           | Entity/Module              | Status  | Notes                                 |
|-----------------|-----------------------------|---------|----------------------------------------|
| Backend         | All primary entities        | Passed  | CRUD + relationship tests complete     |
| Backend         | Auth and JWT handling       | Passed  | Valid and invalid tokens tested        |
| Backend         | Secured endpoints           | Passed  | Proper 401/403 handling verified       |
| Backend         | Error cases and validation  | Passed  | Validation errors tested and handled   |

Total integration test classes: **16**  
Test failures: **0**

### Manual Testing Results

Manual testing was conducted using the Android Emulator to verify real-world behavior and confirm integration between frontend and backend layers.

| Tested Flow                         | Result     | Notes                                      |
|-------------------------------------|------------|--------------------------------------------|
| User signup (full sequence)         | Successful | All data persisted and retrieved correctly |
| Authentication (email and OAuth)   | Successful | JWT stored and reused across screens       |
| Itinerary browsing and saving      | Successful | Dynamic display and next-plan setting work |
| Review submission and fetching     | Successful | Real-time updates reflected on UI          |
| Settings modification              | Successful | PUT and POST behavior verified in UI       |

Manual test passes: **All major user flows validated**  
Blockers or critical issues: **None identified**

### Overall Status

| Area              | Result     |
|-------------------|------------|
| Unit Tests        | ✅ Passed   |
| Integration Tests | ✅ Passed   |
| Manual Testing    | ✅ Passed   |
| Known Failures    | ❌ None     |
| MVP Stability     | ✔ Confirmed|

The system is functionally complete and passes all defined test cases for the MVP scope.

## 12. Next Steps

As the Inclusive Trip Planner progresses beyond the MVP stage, several improvements are planned to strengthen the test strategy, enhance system robustness, and support future scalability. This section outlines concrete actions to guide the next development and QA cycles.

### Test Coverage Improvements

- **Implement End-to-End UI Testing**  
  Introduce automated UI testing using frameworks such as Espresso or UI Automator to simulate full user journeys and detect regressions at the interface level.

- **Expand Failure Path Coverage**  
  Add test cases for edge conditions such as API timeouts, retries, corrupted payloads, and token expiration to ensure resilience under unpredictable network conditions.

- **Introduce Performance and Load Testing**  
  Define test scenarios to simulate multiple concurrent users and large data loads, using tools such as JMeter or Gatling, in order to evaluate system scalability and response times.

- **Accessibility Validation Automation**  
  Integrate accessibility auditing tools (e.g., axe-core, Accessibility Scanner) into the testing workflow to enforce WCAG compliance and screen reader compatibility.

### Infrastructure Enhancements

- **Set Up Continuous Integration**  
  Automate the execution of unit and integration tests on push and pull request events using GitHub Actions or a similar CI service.

- **Prepare for Test Reporting**  
  Integrate test reporting tools (e.g., JaCoCo, Surefire Reports) to generate structured test result dashboards for easier tracking of regressions and coverage metrics.

### Future Feature Testing

Planned features that will require corresponding test coverage include:

- Personalized itinerary recommendations based on user accessibility preferences  
- Travel planning assistant with chat-based interaction  
- Admin interface for itinerary creation and moderation  
- Support for multi-language localization (French and English)  
- Offline and caching mechanisms for reduced connectivity scenarios

### Long-Term QA Goals

- Establish formal acceptance test procedures for releases  
- Define test responsibilities for multi-developer collaboration  
- Introduce versioned test plans aligned with feature roadmaps

The goal is to evolve the current test plan from an MVP validation tool into a long-term quality assurance framework supporting the future of the Inclusive Trip Planner project.
