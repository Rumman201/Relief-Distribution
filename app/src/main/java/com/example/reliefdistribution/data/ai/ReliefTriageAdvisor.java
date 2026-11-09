package com.example.reliefdistribution.data.ai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reliefdistribution.data.cache.InMemoryLruCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Intelligent Relief Request Triage Advisor.
 * Combines contextual disaster NLP keyword extraction with multi-dimensional risk scoring
 * to triage emergency relief requests for dispatcher prioritization.
 * Features strict boundary guardrails, structured validation, and deterministic offline execution.
 */
public class ReliefTriageAdvisor {

    public static final String PRIORITY_CRITICAL = "CRITICAL";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW = "LOW";

    public static final String RISK_INFANT_OR_CHILD = "VULNERABLE_INFANT_OR_CHILD";
    public static final String RISK_ACTIVE_TRAUMA = "ACTIVE_TRAUMA_OR_INJURY";
    public static final String RISK_ELDERLY_OR_DISABLED = "VULNERABLE_ELDERLY_OR_DISABLED";
    public static final String RISK_TRAPPED_IMMINENT_DANGER = "TRAPPED_IMMINENT_DANGER";
    public static final String RISK_MEDICAL_COMPLICATION = "MEDICAL_COMPLICATION";
    public static final String RISK_SEVERE_MALNUTRITION = "SEVERE_MALNUTRITION_RISK";
    public static final String RISK_LONG_WAIT_TIME = "CRITICAL_WAIT_TIME_EXCEEDED";

    // High-performance inference memoization cache (Capacity: 256, TTL: 10 minutes)
    private final InMemoryLruCache<String, TriageAssessment> memoizationCache = new InMemoryLruCache<>(256, 10 * 60 * 1000L);

    /**
     * Strongly-typed evaluation result.
     */
    public static class TriageAssessment {
        private final int urgencyScore;
        private final String priorityTier;
        private final List<String> detectedRiskFactors;
        private final String recommendedAction;
        private final String explanation;
        private final long evaluatedAt;
        private final boolean isOfflineEvaluated;

        public TriageAssessment(int urgencyScore, @NonNull String priorityTier,
                                @NonNull List<String> detectedRiskFactors,
                                @NonNull String recommendedAction,
                                @NonNull String explanation,
                                boolean isOfflineEvaluated) {
            this.urgencyScore = urgencyScore;
            this.priorityTier = priorityTier;
            this.detectedRiskFactors = Collections.unmodifiableList(new ArrayList<>(detectedRiskFactors));
            this.recommendedAction = recommendedAction;
            this.explanation = explanation;
            this.evaluatedAt = System.currentTimeMillis();
            this.isOfflineEvaluated = isOfflineEvaluated;
        }

        public int getUrgencyScore() {
            return urgencyScore;
        }

        @NonNull
        public String getPriorityTier() {
            return priorityTier;
        }

        @NonNull
        public List<String> getDetectedRiskFactors() {
            return detectedRiskFactors;
        }

        @NonNull
        public String getRecommendedAction() {
            return recommendedAction;
        }

        @NonNull
        public String getExplanation() {
            return explanation;
        }

        public long getEvaluatedAt() {
            return evaluatedAt;
        }

        public boolean isOfflineEvaluated() {
            return isOfflineEvaluated;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.US,
                    "TriageAssessment[Score=%d, Tier=%s, Risks=%d, Action='%s']",
                    urgencyScore, priorityTier, detectedRiskFactors.size(), recommendedAction);
        }
    }

    /**
     * Evaluates relief request parameters and computes an intelligent triage assessment.
     */
    @NonNull
    public TriageAssessment evaluate(@Nullable String category,
                                     @Nullable String itemName,
                                     int quantity,
                                     @Nullable String notes,
                                     long createdAt) {
        // Compute discrete age bucket (hours) to enable deterministic cache reuse (clamped >= 0)
        long ageHours = Math.max(0, System.currentTimeMillis() - createdAt) / (3600 * 1000L);
        int safeQty = Math.max(0, quantity);
        String safeNotes = notes != null ? (notes.length() > 2000 ? notes.substring(0, 2000) : notes) : "";
        String cacheKey = (category != null ? category.trim().toLowerCase(Locale.ROOT) : "") + "|"
                + (itemName != null ? itemName.trim().toLowerCase(Locale.ROOT) : "") + "|"
                + safeQty + "|"
                + safeNotes.trim().toLowerCase(Locale.ROOT) + "|"
                + ageHours;

        TriageAssessment cached = memoizationCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        int score = 0;
        List<String> risks = new ArrayList<>();
        StringBuilder explanationBuilder = new StringBuilder();

        // 1. Category Base Urgency
        String catLower = category != null ? category.trim().toLowerCase(Locale.ROOT) : "";
        int categoryWeight;
        if (catLower.contains("medicine") || catLower.contains("medical")) {
            categoryWeight = 40;
            explanationBuilder.append("Medical necessity (+40). ");
        } else if (catLower.contains("water")) {
            categoryWeight = 35;
            explanationBuilder.append("Hydration essential (+35). ");
        } else if (catLower.contains("food")) {
            categoryWeight = 25;
            explanationBuilder.append("Nutrition requirement (+25). ");
        } else if (catLower.contains("shelter")) {
            categoryWeight = 20;
            explanationBuilder.append("Shelter protection (+20). ");
        } else if (catLower.contains("clothing") || catLower.contains("hygiene")) {
            categoryWeight = 15;
            explanationBuilder.append("Sanitation/clothing (+15). ");
        } else {
            categoryWeight = 10;
            explanationBuilder.append("General supplies (+10). ");
        }
        score += categoryWeight;

        // 2. Quantity & Scale Multiplier (Safe non-negative)
        if (safeQty > 50) {
            score += 10;
            explanationBuilder.append("Large group request (+10). ");
        } else if (safeQty > 10) {
            score += 5;
            explanationBuilder.append("Multi-family allocation (+5). ");
        }

        // 3. Keyword Distress Analysis (Natural Language Context with safe length)
        String combinedText = ((itemName != null ? itemName : "") + " " + safeNotes).toLowerCase(Locale.ROOT);

        if (containsAny(combinedText, "trapped", "flood water", "surrounded", "roof", "submerged", "rising water", "drowning")) {
            score += 35;
            risks.add(RISK_TRAPPED_IMMINENT_DANGER);
            explanationBuilder.append("Imminent life danger detected (+35). ");
        }

        if (containsAny(combinedText, "injured", "injury", "bleeding", "wound", "fracture", "hospital", "unconscious", "stroke", "diabetic")) {
            score += 25;
            risks.add(RISK_ACTIVE_TRAUMA);
            explanationBuilder.append("Active trauma or severe illness detected (+25). ");
        }

        if (containsAny(combinedText, "infant", "baby", "newborn", "formula", "child", "children", "pregnant")) {
            score += 20;
            risks.add(RISK_INFANT_OR_CHILD);
            explanationBuilder.append("Infant/child vulnerability (+20). ");
        }

        if (containsAny(combinedText, "elderly", "old age", "senior", "disabled", "wheelchair", "bedridden")) {
            score += 15;
            risks.add(RISK_ELDERLY_OR_DISABLED);
            explanationBuilder.append("Elderly/mobility dependency (+15). ");
        }

        if (containsAny(combinedText, "starving", "starvation", "no food for days", "fainting")) {
            score += 15;
            risks.add(RISK_SEVERE_MALNUTRITION);
            explanationBuilder.append("Severe malnutrition detected (+15). ");
        }

        if (containsAny(combinedText, "dehydration", "fever", "diarrhea", "cholera", "infected")) {
            score += 15;
            risks.add(RISK_MEDICAL_COMPLICATION);
            explanationBuilder.append("Waterborne or infection risk (+15). ");
        }

        // 4. Elapsed Wait Time Escalation
        long now = System.currentTimeMillis();
        long elapsedMillis = Math.max(0, now - createdAt);
        long elapsedHours = elapsedMillis / (1000 * 60 * 60);

        if (elapsedHours >= 24) {
            score += 15;
            risks.add(RISK_LONG_WAIT_TIME);
            explanationBuilder.append("Pending over 24 hours (+15). ");
        } else if (elapsedHours >= 12) {
            score += 10;
            risks.add(RISK_LONG_WAIT_TIME);
            explanationBuilder.append("Pending over 12 hours (+10). ");
        } else if (elapsedHours >= 6) {
            score += 5;
            explanationBuilder.append("Pending over 6 hours (+5). ");
        }

        // 5. Deterministic Guardrails & Bound Clamp
        int clampedScore = Math.min(100, Math.max(0, score));

        // 6. Tier Mapping
        String tier;
        String recommendedAction;
        if (clampedScore >= 80) {
            tier = PRIORITY_CRITICAL;
            recommendedAction = "IMMEDIATE DISPATCH: Assign rapid rescue or priority transport vehicle within 30 minutes.";
        } else if (clampedScore >= 60) {
            tier = PRIORITY_HIGH;
            recommendedAction = "HIGH PRIORITY: Queue for next immediate distributor dispatch run within 2 hours.";
        } else if (clampedScore >= 40) {
            tier = PRIORITY_MEDIUM;
            recommendedAction = "STANDARD DISPATCH: Consolidate into scheduled area distribution route.";
        } else {
            tier = PRIORITY_LOW;
            recommendedAction = "DEFERRED ALLOCATION: Fulfill during secondary replenishment rounds.";
        }

        TriageAssessment result = new TriageAssessment(
                clampedScore,
                tier,
                risks,
                recommendedAction,
                explanationBuilder.toString().trim(),
                true
        );

        memoizationCache.put(cacheKey, result);
        return result;
    }

    public long getCacheHitCount() {
        return memoizationCache.getHitCount();
    }

    public long getCacheMissCount() {
        return memoizationCache.getMissCount();
    }

    public double getCacheHitRatio() {
        return memoizationCache.getHitRatio();
    }

    public void clearCache() {
        memoizationCache.clear();
    }

    /**
     * Validates whether an incoming triage assessment satisfies mathematical and schema invariants.
     */
    public static boolean isValidAssessment(@Nullable TriageAssessment assessment) {
        if (assessment == null) return false;
        if (assessment.getUrgencyScore() < 0 || assessment.getUrgencyScore() > 100) return false;
        String tier = assessment.getPriorityTier();
        if (!PRIORITY_CRITICAL.equals(tier) && !PRIORITY_HIGH.equals(tier)
                && !PRIORITY_MEDIUM.equals(tier) && !PRIORITY_LOW.equals(tier)) {
            return false;
        }
        if (assessment.getRecommendedAction().isEmpty()) return false;
        return assessment.getEvaluatedAt() > 0;
    }

    private static boolean containsAny(@NonNull String target, @NonNull String... keywords) {
        for (String kw : keywords) {
            if (target.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
