package org.example.refundStatusSync;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface RefundSyncWorkflow {
    @WorkflowMethod
    void syncRefundStatus();
}
