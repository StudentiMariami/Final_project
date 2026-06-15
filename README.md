# 📝 AFinal — Notes App

A fully-featured Android notes application built as a final exam project.

---

## 📱 Application Description

AFinal is a personal notes manager where users can:
- **Create** notes with a title, content, and optional image URL
- **Browse** notes in a scrollable list
- **View** the full detail of any note (with image loaded from the web)
- **Delete** notes with a confirmation dialog
- **Sync** notes from a remote REST API into local storage

---

## 🏗️ Technical Architecture: MVVM

The app strictly follows the **MVVM (Model-View-ViewModel)** architecture pattern:

```
UI Layer (View)
    │  observes LiveData
    ▼
ViewModel
    │  calls repository methods
    ▼
Repository  ◄──────────────────┐
    │                          │
    ▼                          ▼
Room (local DB)         Retrofit (remote API)
```

| Layer | Classes | Responsibility |
|-------|---------|----------------|
| **Model** | `Note.kt` | Data class + Room Entity |
| **View** | `MainActivity`, `HomeFragment`, `AddFragment`, `DetailFragment` | UI display only |
| **ViewModel** | `NoteViewModel` | Business logic, LiveData, coroutines |
| **Repository** | `NoteRepository` | Single source of truth for data |
| **Local DB** | `NoteDatabase`, `NoteDao` | SQLite via Room |
| **Remote** | `ApiService`, `RetrofitInstance` | HTTP via Retrofit |

---

## 🔧 Technical Details

### Libraries Used

| Library | Version | Purpose |
|---------|---------|---------|
| **Room** | 2.6.1 | Local SQLite database with ORM |
| **Retrofit** | 2.9.0 | HTTP REST API client |
| **Glide** | 4.16.0 | ⭐ Image loading from URLs (NEW FEATURE) |
| **Navigation Component** | 2.7.7 | Fragment navigation + back stack |
| **LiveData** | 2.7.0 | Observable data for MVVM |
| **ViewModel** | 2.7.0 | Survives configuration changes |
| **Coroutines** | 1.7.3 | Async operations (no blocking UI) |
| **ViewBinding** | built-in | Type-safe view access (NO findViewById) |
| **Material Components** | 1.12.0 | Cards, FAB, TextInputLayout |

### Key Technical Decisions

- **ViewBinding** is used everywhere instead of `findViewById` (which is forbidden)
- **Single Activity Architecture**: one `MainActivity` hosts all Fragments
- **Safe Args**: type-safe argument passing between Fragments via Navigation Component
- **DiffUtil**: RecyclerView only redraws items that actually changed
- **Coroutines + Dispatchers.IO**: all database and network calls run off the main thread
- **Singleton Pattern**: both Room database and Retrofit instance are singletons

### New Feature: Glide Image Loading 🖼️

Glide was not used in any previous lecture. It handles:
- Downloading images from URLs on a background thread
- Caching images to memory and disk (fast reloads)
- Showing a placeholder while the image loads
- Showing an error icon if the image fails
- Smooth image transitions and `thumbnail()` for progressive loading

### Database

Room creates a local SQLite database called `note_database` with one table: `notes`

| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK) | Auto-generated unique ID |
| `title` | TEXT | Note title |
| `content` | TEXT | Note body text |
| `imageUrl` | TEXT | Optional image URL for Glide |
| `timestamp` | INTEGER | Creation time (milliseconds) |

### Navigation Graph

```
HomeFragment  ──[tap note]──►  DetailFragment
     │
     └──[tap FAB]──►  AddFragment
```

---

## ✅ Exam Requirements Checklist

- [x] **Menu** — Toolbar menu in `HomeFragment` (Sync + About)
- [x] **List** — `RecyclerView` with `ListAdapter` + `DiffUtil` in `HomeFragment`
- [x] **MVVM Architecture** — `NoteViewModel` + `NoteRepository` + `LiveData`
- [x] **Database** — Room (local) + Retrofit (remote API sync)
- [x] **New Feature** — Glide image loading (never used in lectures)
- [x] **README** — this file
- [x] **No `findViewById`** — ViewBinding used throughout
- [x] **No XML layouts with `Fragment` tag** — uses `FragmentContainerView`

---

## 🚀 How to Run

1. Clone the repository
2. Open in Android Studio (Hedgehog or newer)
3. Wait for Gradle sync to complete
4. Run on emulator or physical device (API 26+)

> **Note:** Firebase is included as a dependency but requires a `google-services.json` file for actual use. The app works without it using Room + Retrofit only.
