# Mobile Backend Alignment

This app consumes the Spring Boot backend in `../BE_old_bicycle_project/old_bicycle_project/`.

## Relevant Existing Backend Areas

- auth: `/api/auth/*`
- products: `/api/products`
- orders: `/api/orders/*`
- payments: `/api/payments/*`

## Auth Expectations

- access token and refresh token come from login response
- keep access token short-lived in memory when possible
- keep refresh token in encrypted storage
- use `Authorization: Bearer <token>`

## Mobile API Rules

- do not invent DTO fields when backend already defines them
- use backend pagination shape as-is first
- centralize the base URL
- show user-friendly error messages, not raw stack traces

## MVP Scope Fit

Good first mobile slices for this project:

1. login and profile
2. product list and detail
3. wishlist
4. order creation and order history

