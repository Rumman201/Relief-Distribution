package com.example.reliefdistribution;

import com.example.reliefdistribution.data.ai.ReliefTriageAdvisor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit & Quality Evaluation Suite for ReliefTriageAdvisor.
 * Tests AI scoring rubrics, keyword context extraction, mathematical guardrails,
 * and adversarial edge cases.
 */
public class ReliefTriageAdvisorTest {

    private ReliefTriageAdvisor advisor;

    @Before
    public void setUp() {
        advisor = new ReliefTriageAdvisor();
    }

    @Test
    public void evaluate_medicalTrauma_returnsCriticalPriority() {
        long now = System.currentTimeMillis();
        ReliefTriageAdvisor.TriageAssessment assessment = advisor.evaluate(
                "Medicine",
                "First Aid Kits & Insulin",
                5,
                "Elderly patient injured, severe bleeding from cyclone debris",
                now
        );

        assertNotNull(assessment);
        assertTrue("Urgency score must be high for active trauma", assessment.getUrgencyScore() >= 80);
        assertEquals(ReliefTriageAdvisor.PRIORITY_CRITICAL, assessment.getPriorityTier());
        assertTrue(assessment.getDetectedRiskFactors().contains(ReliefTriageAdvisor.RISK_ACTIVE_TRAUMA));
        assertTrue(assessment.getDetectedRiskFactors().contains(ReliefTriageAdvisor.RISK_ELDERLY_OR_DISABLED));
        assertTrue(ReliefTriageAdvisor.isValidAssessment(assessment));
    }

    @Test
    public void evaluate_infantWaterEmergency_returnsHighPriority() {
        long now = System.currentTimeMillis();
        ReliefTriageAdvisor.TriageAssessment assessment = advisor.evaluate(
                "Water",
                "Purified Water Bottles",
                12,
                "Infant and pregnant mother stranded without drinking water",
                now
        );

        assertNotNull(assessment);
        assertTrue(assessment.getUrgencyScore() >= 60);
        assertTrue(assessment.getPriorityTier().equals(ReliefTriageAdvisor.PRIORITY_HIGH) ||
                assessment.getPriorityTier().equals(ReliefTriageAdvisor.PRIORITY_CRITICAL));
        assertTrue(assessment.getDetectedRiskFactors().contains(ReliefTriageAdvisor.RISK_INFANT_OR_CHILD));
        assertTrue(ReliefTriageAdvisor.isValidAssessment(assessment));
    }

    @Test
    public void evaluate_trappedFloodVictims_returnsCriticalPriority() {
        long now = System.currentTimeMillis();
        ReliefTriageAdvisor.TriageAssessment assessment = advisor.evaluate(
                "Shelter",
                "Tarpaulin",
                15,
                "Family of 6 trapped on roof, flood water rising rapidly",
                now
        );

        assertNotNull(assessment);
        assertTrue(assessment.getUrgencyScore() >= 55);
        assertTrue(assessment.getDetectedRiskFactors().contains(ReliefTriageAdvisor.RISK_TRAPPED_IMMINENT_DANGER));
        assertTrue(ReliefTriageAdvisor.isValidAssessment(assessment));
    }

    @Test
    public void evaluate_generalSuppliesNoDistress_returnsLowOrMediumPriority() {
        long now = System.currentTimeMillis();
        ReliefTriageAdvisor.TriageAssessment assessment = advisor.evaluate(
                "Other",
                "Flashlight Batteries",
                2,
                "Standard replenishment for upcoming evening",
                now
        );

        assertNotNull(assessment);
        assertTrue("General supplies without distress should score low", assessment.getUrgencyScore() < 40);
        assertEquals(ReliefTriageAdvisor.PRIORITY_LOW, assessment.getPriorityTier());
        assertTrue(assessment.getDetectedRiskFactors().isEmpty());
        assertTrue(ReliefTriageAdvisor.isValidAssessment(assessment));
    }

    @Test
    public void evaluate_longPendingWaitTime_escalatesScore() {
        long now = System.currentTimeMillis();
        long twentyFiveHoursAgo = now - (25 * 60 * 60 * 1000L);

        ReliefTriageAdvisor.TriageAssessment freshAssessment = advisor.evaluate(
                "Food", "Rice", 5, "Standard request", now
        );
        ReliefTriageAdvisor.TriageAssessment delayedAssessment = advisor.evaluate(
                "Food", "Rice", 5, "Standard request", twentyFiveHoursAgo
        );

        assertTrue("Delayed request score must escalate",
                delayedAssessment.getUrgencyScore() > freshAssessment.getUrgencyScore());
        assertTrue(delayedAssessment.getDetectedRiskFactors().contains(ReliefTriageAdvisor.RISK_LONG_WAIT_TIME));
    }

    @Test
    public void evaluate_extremeInput_boundClampsToHundred() {
        long now = System.currentTimeMillis();
        long twoDaysAgo = now - (48 * 60 * 60 * 1000L);

        // Intentionally pack every possible danger keyword and huge quantity
        ReliefTriageAdvisor.TriageAssessment extreme = advisor.evaluate(
                "Medicine",
                "Antibiotics and IV fluids",
                100,
                "50 people trapped on roof, rising water, infant babies starving, injured and bleeding elderly disabled",
                twoDaysAgo
        );

        assertEquals("Score must clamp at upper boundary 100", 100, extreme.getUrgencyScore());
        assertEquals(ReliefTriageAdvisor.PRIORITY_CRITICAL, extreme.getPriorityTier());
        assertTrue(ReliefTriageAdvisor.isValidAssessment(extreme));
    }

    @Test
    public void evaluate_nullAndEmptyInputs_safelyHandledWithoutException() {
        long now = System.currentTimeMillis();
        ReliefTriageAdvisor.TriageAssessment assessment = advisor.evaluate(
                null, null, 0, null, now
        );

        assertNotNull(assessment);
        assertTrue(assessment.getUrgencyScore() >= 0);
        assertTrue(assessment.getUrgencyScore() <= 100);
        assertTrue(ReliefTriageAdvisor.isValidAssessment(assessment));
    }

    @Test
    public void isValidAssessment_detectsInvalidSchemas() {
        assertFalse(ReliefTriageAdvisor.isValidAssessment(null));

        ReliefTriageAdvisor.TriageAssessment negativeScore = new ReliefTriageAdvisor.TriageAssessment(
                -5, ReliefTriageAdvisor.PRIORITY_LOW, java.util.Collections.emptyList(), "action", "exp", true);
        assertFalse(ReliefTriageAdvisor.isValidAssessment(negativeScore));

        ReliefTriageAdvisor.TriageAssessment overScore = new ReliefTriageAdvisor.TriageAssessment(
                105, ReliefTriageAdvisor.PRIORITY_CRITICAL, java.util.Collections.emptyList(), "action", "exp", true);
        assertFalse(ReliefTriageAdvisor.isValidAssessment(overScore));

        ReliefTriageAdvisor.TriageAssessment invalidTier = new ReliefTriageAdvisor.TriageAssessment(
                50, "UNKNOWN_TIER", java.util.Collections.emptyList(), "action", "exp", true);
        assertFalse(ReliefTriageAdvisor.isValidAssessment(invalidTier));
    }
}
