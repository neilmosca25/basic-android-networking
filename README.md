# Basic of Android Networking

A simple Android application for managing a Movie catalog, implementing full CRUD (Create, Read, Update, Delete) operations. This project serves as a demonstration of different networking implementations in Android using **Retrofit** and **Ktor**.

Using this [RESTful API](https://github.com/neilmosca25/basic-rest-api-express-mongodb), the application demonstrates how to interact with a remote backend.

---

## Branch Overview

This repository is organized into three main branches to showcase different data management strategies:

### 1. `main` Branch (In-Memory Implementation)
The `main` branch contains the core UI built with **Jetpack Compose** and a **MVI-inspired Architecture**.
- **Data Source**: Uses a non-persistent, in-memory `mutableListOf<Movie>` within a Singleton `MoviesApi` class.
- **Key Features**: 
  - CRUD operations logic without network latency.
  - UI state management using `StateFlow` in `MovieViewModel`.
  - Floating Action Button (FAB) and Dialogs for movie entry.
  - Edit and Delete actions directly on the list items.

### 2. `retrofit` Branch (Retrofit + Gson)
This branch refactors the `main` branch to use **Retrofit** for network communication.
- **Implementation**:
  - Defines a `MoviesApi` interface with Retrofit annotations (`@GET`, `@POST`, `@PUT`, `@DELETE`).
  - Uses `GsonConverterFactory` for JSON parsing.
  - Integrates `Response<T>` to handle HTTP status codes and validation.
- **Changes from `main`**:
  - Replaces local list logic with asynchronous suspend functions.
  - Adds Internet permissions to `AndroidManifest.xml`.
  - Adds dependencies for `retrofit2` and `converter-gson`.

### 3. `ktor` Branch (Ktor + Kotlinx Serialization)
This branch demonstrates the modern, multiplatform-ready **Ktor** HTTP client.
- **Implementation**:
  - Uses `HttpClient` with the `CIO` engine.
  - Configures `ContentNegotiation` with `kotlinx.serialization` for JSON handling.
  - Implements logging using Ktor's `Logging` plugin for debugging network traffic.
- **Changes from `main`**:
  - Replaces the Retrofit interface with a `MoviesApi` class that utilizes the `HttpClient`.
  - Replaces `Gson` with `kotlinx.serialization` annotations on the `Movie` data class.
  - Offers more granular control over the request/response pipeline compared to Retrofit.

---

## Technical Stack
- **UI**: Jetpack Compose
- **Architecture**: ViewModel + StateFlow (MVI/MVVM)
- **Networking**: Retrofit / Ktor
- **JSON Parsing**: Gson / Kotlinx Serialization
- **Concurrency**: Kotlin Coroutines

## Setup
1. Clone the repository.
2. Checkout the branch you want to explore:
   ```bash
   git checkout retrofit
   # or
   git checkout ktor
   ```
3. Ensure you have a running instance of the [Backend API](https://github.com/neilmosca25/basic-rest-api-express-mongodb) or update the `BASE_URL` in `MoviesApi` to point to your endpoint.
