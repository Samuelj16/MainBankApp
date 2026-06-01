# MainBankApp

A single-flow console banking application written in Java. Demonstrates the four core OOP pillars — encapsulation, inheritance, abstraction, and polymorphism — through a simple admin/customer banking system.

## Flow

```
main() -> welcome() -> login() -> adminDashboard() OR customerDashboard()
```

## Features

### Admin
- View all customers
- View all accounts
- Add, update, delete customers
- Add or delete accounts for a customer

### Customer
- View own accounts and balances
- Deposit and withdraw funds
- Apply interest (Savings 3%, Checking 2%)

## Domain Model

- `User` (base) → `Customer`, `Admin`
- `Account` (abstract) → `SavingsAccount`, `CheckingAccount`
- `addInterest()` is abstract on `Account` and resolved polymorphically at runtime

## Default Credentials

| Role     | Username  | Password    |
|----------|-----------|-------------|
| Admin    | `admin`   | `admin123`  |
| Customer | `rohit`   | `rohit123`  |
| Customer | `mohit`   | `mohit123`  |
| Customer | `shobhit` | `shobhit123`|

Seeded accounts: `rohit` has a Savings account ($5000); `mohit` has a Checking account ($1200).

## Running

### From the command line

```bash
cd src
javac MainBankApp.java
java MainBankApp
```

At the login prompt, enter username and password separated by a space:

```
admin admin123
```

### From IntelliJ

Open this folder as a project and run `MainBankApp`.

## Requirements

- Java 8 or later

## Project Structure

```
.
├── README.md
├── .gitignore
├── untitled.iml
└── src/
    └── MainBankApp.java
```
