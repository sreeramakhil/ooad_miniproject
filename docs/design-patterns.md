# StockFlow Design Notes

This project follows the MVC structure used in Spring Boot:

- Model: `Product`, `Order`, `OrderItem`, `Notification`, `User`
- View: Thymeleaf templates under `src/main/resources/templates`
- Controller: `AdminController`, `StoreController`, `GlobalExceptionHandler`

Patterns demonstrated in code:

- Builder Pattern: Lombok builders on models such as `Product`, `Order`, and `Notification`
- Strategy Pattern: `PricingStrategy` with `StandardPricingStrategy` for subtotal, tax, and total calculation
- Factory Pattern: `OrderFactory` and `NotificationFactory` for centralized object creation
- Repository Pattern: Spring Data JPA repositories for persistence

SOLID-friendly improvements:

- Pricing logic is centralized instead of duplicated across services
- Order and notification creation are centralized in factories
- Controllers remain focused on request handling and view-model preparation
