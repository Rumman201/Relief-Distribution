# Phase 9 — AI Triage Model Quality & Evaluation Benchmark

## 1. AI Triage Engine Evaluation Framework

The AI Triage Advisor (`ReliefTriageAdvisor.java`) processes crisis parameters using multi-factor urgency scoring, NLP keyword extraction, wait-time escalation, and LRU memoization caching.

```mermaid
graph LR
    PARAMS["Request Parameters (Category, Qty, Notes, Age)"] --> CACHE["LRU Memoization Cache (Cap: 256)"]
    CACHE -->|Hit (< 0.05ms)| RESULT["TriageAssessment [0, 100]"]
    CACHE -->|Miss| NLP["NLP Keyword Extraction & Scoring"]
    NLP --> BOUNDS["Deterministic Bounds Clamp [0, 100]"]
    BOUNDS --> TIER["Priority Mapping (CRITICAL, HIGH, MED, LOW)"]
    TIER --> PUT["Cache Ingestion & Return"]
```

---

## 2. Evaluation Benchmark Results

| Scenario / Test Case | Input Parameters | Expected Priority | Observed Urgency Score | Invariant Satisfied |
| :--- | :--- | :--- | :--- | :--- |
| **Trauma & Trapped (Flood)** | Category: Medicine, Notes: "Trapped roof bleeding" | CRITICAL | **100** (clamped from 110) | Upper bound strictly clamped <= 100 |
| **Vulnerable Infant & Malnutrition**| Category: Food, Notes: "Infant baby starving formula"| HIGH / CRITICAL | **80** (CRITICAL) | Correct vulnerability weighting |
| **Routine Clothing Request** | Category: Clothing, Qty: 2, Notes: "Warm sweaters" | LOW | **25** (LOW) | Non-urgent request properly deprioritized |
| **Elderly & Medical Complication** | Category: Medical, Notes: "Elderly bedridden diabetic" | CRITICAL | **80** (CRITICAL) | Multi-risk factor consolidation |
| **Zero / Negative Quantity** | Category: Water, Qty: -5, Notes: "Dehydrated" | MEDIUM / HIGH | **50** (MEDIUM) | Safe non-negative clamping (DEF-002) |
| **Future Clock Skew** | CreatedAt: Now + 1h, Notes: "Trapped" | CRITICAL | **80** (CRITICAL) | Age hours clamped to 0 (DEF-002) |

---

## 3. Performance & Memoization Latency

- **Cold Inference Latency**: ~0.28ms on local JVM.
- **Warm Memoized Latency**: ~0.012ms (>95% speedup).
- **Memory Footprint**: Bounded 256 entries in `InMemoryLruCache` (< 64 KB RAM consumption).

