# MyWalletApp

A digital wallet API. Users sign up and log in with JWT auth, then create wallets, fund them, transfer money between wallets, withdraw, and view transaction history.

## Stack

Java, Spring Boot, Spring Security with JWT, Spring Data JPA, Postgres, Lombok.

## Features

- Sign up and login with JWT-based authentication
- Role-based user accounts (seeded on startup)
- Create a wallet for a user
- Fund a wallet
- Transfer funds between wallets, including between different users
- Withdraw from a wallet
- Check wallet balance
- Paginated transaction history

## Running it

You'll need a local Postgres database. Update the connection details in `src/main/resources/application.properties`, then:

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8098`.

## API overview

All endpoints are under `/api`.

**Auth**
- `POST /auth/signup`
- `POST /auth/login`

**Wallets**
- `POST /wallets/{userId}/add` – create a wallet
- `POST /wallets/{userId}/fund/{walletId}` – fund a wallet
- `POST /wallets/{fromUserId}/transfer/{fromWalletId}/to/{toUserId}/wallet/{toWalletId}` – transfer funds
- `POST /wallets/{userId}/withdraw/{walletId}` – withdraw
- `GET /wallets/{userId}/balance/{walletId}` – check balance
- `GET /wallets/{userId}/transactions` – paginated transaction history

Send the JWT from login as `Authorization: Bearer <token>` on protected requests.
