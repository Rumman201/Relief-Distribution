package com.example.reliefdistribution;

import com.example.reliefdistribution.data.ai.ReliefTriageAdvisor;
import com.example.reliefdistribution.data.automation.ReliefWorkflowAutomator;
import com.example.reliefdistribution.data.iot.OfflineTelemetryBuffer;
import com.example.reliefdistribution.data.model.LocationTelemetry;
import com.example.reliefdistribution.data.resilience.NetworkResilienceCircuitBreaker;
import com.example.reliefdistribution.data.util.SecuritySanitizer;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Phase 9 Comprehensive Full-System Quality Assurance & Verification Suite.
 * Covers:
 * 1. Autonomous defect remediation regression tests (DEF-001 through DEF-004).
 * 2. Full end-to-end multi-step survivor to distributor aid journey.
 * 3. Chaos and resilience circuit breaker fault injection.
 * 4. IoT edge telemetry compaction and bounded queue invariants.
 */
public class FullSystemQualityAssuranceTest {

    private ReliefWorkflowAutomator automator;
    private ReliefTriageAdvisor triageAdvisor;
    private OfflineTelemetryBuffer telemetryBuffer;

    @Before
    public void setUp() {
        automator = new ReliefWorkflowAutomator();
        triageAdvisor = new ReliefTriageAdvisor();
        telemetryBuffer = new OfflineTelemetryBuffer(50);
    }

    // -------------------------------------------------------------------------
    // 1. DEFECT REMEDIATION REGRESSION TESTS (DEF-001 to DEF-004)
    // -------------------------------------------------------------------------

    @Test
    public void testDefect001_automatorRejectsNonPositiveQuantities() {
        ReliefRequest zeroQtyRequest = new ReliefRequest(
                "surv-01", "Survivor Zero", "01711111111",
                23.81, 90.41, "Zone 1", "Water", "Water", 0, "High", "Needs water"
        );
        zeroQtyRequest.setRequestId("req-zero");
        zeroQtyRequest.setStatus(ReliefRequest.STATUS_PENDING);

        InventoryItem inventory = new InventoryItem("dist-01", "Water", "Water", 100, "Liters", "Depot A");
        inventory.setItemId("inv-water");

        ReliefWorkflowAutomator.AutomationResult result = automator.onDistributorAcceptRequest(
                zeroQtyRequest, inventory, "dist-01", "Alpha Team"
        );

        assertFalse("DEF-001: Automator must reject request with zero quantity", result.isSuccess());
        assertTrue(result.getErrorMessage().contains("non-positive quantity"));
        assertEquals("Available inventory must not change on rejection", 100, inventory.getQuantityAvailable());
        assertEquals("Reserved inventory must not change on rejection", 0, inventory.getQuantityReserved());

        // Negative quantity test
        zeroQtyRequest.setQuantity(-10);
        ReliefWorkflowAutomator.AutomationResult negResult = automator.onDistributorAcceptRequest(
                zeroQtyRequest, inventory, "dist-01", "Alpha Team"
        );
        assertFalse("DEF-001: Automator must reject request with negative quantity", negResult.isSuccess());
    }

    @Test
    public void testDefect002_triageAdvisorHandlesFutureClockSkewAndAdversarialNotes() {
        // Future timestamp skew test (clock mismatch)
        long futureTime = System.currentTimeMillis() + 3_600_000L; // 1 hour into future
        ReliefTriageAdvisor.TriageAssessment assessmentFuture = triageAdvisor.evaluate(
                "Medical", "Insulin", -5, "Trapped on roof with severe bleeding", futureTime
        );

        assertNotNull(assessmentFuture);
        assertTrue("Urgency score must be within [0, 100]", assessmentFuture.getUrgencyScore() >= 0 && assessmentFuture.getUrgencyScore() <= 100);
        assertTrue("Priority must be CRITICAL due to trauma and trapped keywords",
                ReliefTriageAdvisor.PRIORITY_CRITICAL.equals(assessmentFuture.getPriorityTier()));

        // Adversarial huge notes string (10,000 characters)
        StringBuilder hugeNotes = new StringBuilder();
        hugeNotes.append("trapped flood water ");
        for (int i = 0; i < 500; i++) {
            hugeNotes.append("EMERGENCY ATTACK PAYLOAD ");
        }

        ReliefTriageAdvisor.TriageAssessment assessmentHuge = triageAdvisor.evaluate(
                "Food", "Emergency Rice", 10, hugeNotes.toString(), System.currentTimeMillis()
        );
        assertNotNull(assessmentHuge);
        assertTrue(assessmentHuge.getUrgencyScore() <= 100);
        assertTrue(assessmentHuge.getDetectedRiskFactors().contains(ReliefTriageAdvisor.RISK_TRAPPED_IMMINENT_DANGER));
    }

    @Test
    public void testDefect003_sanitizerHandlesZeroAndNegativeMaxLength() {
        String input = "Hello <script>alert(1)</script> World";

        assertEquals("DEF-003: Negative maxLength must return empty string without throwing exception",
                "", SecuritySanitizer.sanitizeInput(input, -5));

        assertEquals("DEF-003: Zero maxLength must return empty string without throwing exception",
                "", SecuritySanitizer.sanitizeInput(input, 0));

        assertEquals("DEF-003: Positive maxLength works properly",
                "Hello", SecuritySanitizer.sanitizeInput(input, 5));
    }

    @Test
    public void testDefect004_inventoryItemClampsNegativeQuantities() {
        InventoryItem item = new InventoryItem("dist-1", "Tents", "Shelter", 50, "Units", "Depot B");

        item.setQuantityAvailable(-25);
        assertEquals("DEF-004: Available quantity must clamp to 0 on negative input", 0, item.getQuantityAvailable());

        item.setQuantityReserved(-10);
        assertEquals("DEF-004: Reserved quantity must clamp to 0 on negative input", 0, item.getQuantityReserved());
        assertEquals(0, item.getQuantityTotal());
        assertEquals(InventoryItem.STATUS_OUT_OF_STOCK, item.getStatus());
    }

    // -------------------------------------------------------------------------
    // 2. FULL-SYSTEM END-TO-END DISASTER RELIEF JOURNEY
    // -------------------------------------------------------------------------

    @Test
    public void testFullSystem_endToEndSurvivorToDistributorJourney() {
        // Step 1: Survivor creates emergency aid request
        ReliefRequest request = new ReliefRequest(
                "surv-99", "Survivor Fatima", "01811223344",
                24.8949, 91.8687, "Sylhet Flood Relief Zone 4",
                "Emergency Rations", "Food", 12, "high",
                "Family of 6 with 2 infants trapped by rising flood water"
        );
        request.setRequestId("req-journey-01");
        request.setStatus(ReliefRequest.STATUS_PENDING);

        // Step 2: AI Triage Engine analyzes urgency
        ReliefTriageAdvisor.TriageAssessment triage = triageAdvisor.evaluate(
                request.getItemCategory(), request.getItemName(), request.getQuantity(),
                request.getNotes(), request.getTimestamp()
        );

        assertNotNull(triage);
        assertTrue("Urgency score must reflect trapped and infant risks (>= 80)", triage.getUrgencyScore() >= 80);
        assertEquals(ReliefTriageAdvisor.PRIORITY_CRITICAL, triage.getPriorityTier());
        request.setAiUrgencyScore(triage.getUrgencyScore());
        request.setAiPriorityTier(triage.getPriorityTier());

        // Step 3: Distributor reviews inventory depot and accepts request
        InventoryItem depotStock = new InventoryItem(
                "dist-44", "Emergency Rations", "Food", 50, "Bags", "Sylhet Depot Alpha"
        );
        depotStock.setItemId("inv-rations-44");

        ReliefWorkflowAutomator.AutomationResult acceptResult = automator.onDistributorAcceptRequest(
                request, depotStock, "dist-44", "Captain Tariq"
        );

        assertTrue("Distributor acceptance must succeed", acceptResult.isSuccess());
        assertEquals(ReliefRequest.STATUS_ACCEPTED, request.getStatus());
        assertEquals("dist-44", request.getDistributorId());
        assertEquals("Available stock must decrement by 12", 38, depotStock.getQuantityAvailable());
        assertEquals("Reserved stock must increment by 12", 12, depotStock.getQuantityReserved());

        Notification acceptNotif = acceptResult.getGeneratedNotification();
        assertNotNull(acceptNotif);
        assertEquals("surv-99", acceptNotif.getUserId());
        assertEquals(Notification.TYPE_REQUEST_ACCEPTED, acceptNotif.getType());

        // Step 4: Distributor departs depot and starts delivery
        ReliefWorkflowAutomator.AutomationResult startResult = automator.onStartDelivery(request);
        assertTrue(startResult.isSuccess());
        assertEquals(ReliefRequest.STATUS_IN_PROGRESS, request.getStatus());

        // Step 5: Distributor reaches survivor coordinates and fulfills relief distribution
        ReliefWorkflowAutomator.AutomationResult completeResult = automator.onDeliveryCompleted(request, depotStock);
        assertTrue("Delivery completion must succeed", completeResult.isSuccess());
        assertEquals(ReliefRequest.STATUS_COMPLETED, request.getStatus());
        assertEquals("Reserved stock must be released on delivery completion", 0, depotStock.getQuantityReserved());
        assertEquals(38, depotStock.getQuantityAvailable());

        Notification completeNotif = completeResult.getGeneratedNotification();
        assertNotNull(completeNotif);
        assertEquals(Notification.TYPE_REQUEST_COMPLETED, completeNotif.getType());
    }

    // -------------------------------------------------------------------------
    // 3. CHAOS, FAULT TOLERANCE & CIRCUIT BREAKER VERIFICATION
    // -------------------------------------------------------------------------

    @Test
    public void testChaosAndFaultTolerance_circuitBreakerTripsAndRecovers() {
        NetworkResilienceCircuitBreaker breaker = new NetworkResilienceCircuitBreaker(
                3, 50L // 3 failures, 50ms cool-down
        );

        assertTrue("Initially circuit breaker must be CLOSED", breaker.canExecute());

        // Inject 3 consecutive synthetic network failures
        breaker.recordFailure();
        breaker.recordFailure();
        assertEquals(NetworkResilienceCircuitBreaker.State.CLOSED, breaker.getState());

        breaker.recordFailure(); // 3rd failure trips the breaker
        assertEquals(NetworkResilienceCircuitBreaker.State.OPEN, breaker.getState());
        assertFalse("Requests must be blocked when OPEN", breaker.canExecute());

        // Wait for cool-down to elapse
        try {
            Thread.sleep(60L);
        } catch (InterruptedException ignored) {}

        // Half-open transition verification
        assertTrue("After cool-down, should allow single probe request", breaker.canExecute());
        assertEquals(NetworkResilienceCircuitBreaker.State.HALF_OPEN, breaker.getState());

        // Probe succeeds
        breaker.recordSuccess();
        assertEquals("Circuit breaker must reset to CLOSED after successful recovery",
                NetworkResilienceCircuitBreaker.State.CLOSED, breaker.getState());
        assertTrue(breaker.canExecute());
    }

    // -------------------------------------------------------------------------
    // 4. IOT EDGE TELEMETRY COMPACTION & BUFFER BOUNDS
    // -------------------------------------------------------------------------

    @Test
    public void testIotEdgeTelemetry_stationaryCompactionAndBufferBounds() {
        long baseTime = 1700000000000L;

        // Stationary coordinate stream (velocity < 0.5 m/s, delta < 5m)
        for (int i = 0; i < 20; i++) {
            LocationTelemetry stationaryFix = new LocationTelemetry(
                    "dist-99", 23.810300, 90.412500, baseTime + (i * 5000L), 3.0f, 0.1f, true
            );
            telemetryBuffer.enqueueWithCompaction(stationaryFix);
        }

        // Compaction should preserve queue space: 20 stationary fixes compacted into 1 slot
        assertEquals("Stationary fixes should be compacted into a single entry", 1, telemetryBuffer.size());

        // Transit movement (velocity > 5 m/s, varying coordinates)
        for (int i = 1; i <= 10; i++) {
            LocationTelemetry transitFix = new LocationTelemetry(
                    "dist-99", 23.810300 + (i * 0.001), 90.412500 + (i * 0.001),
                    baseTime + 100_000L + (i * 5000L), 3.0f, 12.0f, true
            );
            telemetryBuffer.enqueueWithCompaction(transitFix);
        }

        assertEquals("Transit coordinates must be preserved in sequence", 11, telemetryBuffer.size());

        // Drain test
        List<LocationTelemetry> drained = telemetryBuffer.drainAll();
        assertEquals(11, drained.size());
        assertTrue("Buffer must be empty after drainAll", telemetryBuffer.isEmpty());
    }
}
