# ant-man-2026

Demo trading UI: **Hyper** (server-rendered reactive HTML + SSE), **Missionary** (`ap` simulators), streamed in-memory positions and trades.

## Requirements

- JDK **21+** (Hyper uses virtual threads)
- Clojure CLI

## Run

```bash
clojure -M:run
```

Open:

- http://localhost:3000/trading — full-page positions and trades tables
- http://localhost:3000/layout — Golden Layout v2 workspace with panel tabs

Tables update live over SSE as the simulators tick.

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

No database in v1 — simulators write to `positions*` and `trades*` atoms; Hyper watches push HTML updates to the browser.
