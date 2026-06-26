# ant-man-2026

Demo trading UI: **Hyper** (server-rendered reactive HTML + SSE), **Missionary** (`ap` simulators), streamed in-memory positions and trades.

## Requirements

- JDK **21+** (Hyper uses virtual threads)
- Clojure CLI

## Run

```bash
clojure -X:run
```

Services are defined in `resources/services.edn` and started via [modular](https://github.com/pink-gorilla/modular) / juxt.clip.

Configuration:

- `resources/config.edn` — web server port, token/OAuth2, logging
- `resources/users.edn` — local user accounts (hashed passwords)

Open:

- http://localhost:3000/trading — full-page positions and trades tables
- http://localhost:3000/layout — Golden Layout v2 workspace with panel tabs

Tables update live over SSE as the simulators tick.

nREPL listens on port **9100** when the app is running.

## Users and passwords

`token` compares login passwords against **pre-hashed** values in `users.edn` (blake2b-128 hex). Passwords are not hashed on each app start.

To add or change a user:

1. Edit `resources/users.edn` with plain-text `:password` values.
2. Run `clojure -X:hash-users` once to write hashes back to the file.
3. Start the app with `clojure -X:run`.

## Test

```bash
clojure -M:test
```

## Stack

| Layer | Library |
|-------|---------|
| Web | [Hyper](https://github.com/dynamic-alpha/hyper) |
| Reactive sim | [Missionary](https://github.com/leonoel/missionary) |
| Layout | [Golden Layout](https://golden-layout.github.io/golden-layout/) v2 |
| Services | [modular](https://github.com/pink-gorilla/modular) |

No database in v1 — simulators write to `positions*` and `trades*` atoms; Hyper watches push HTML updates to the browser.
