package org.example.refundStatusSync;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class RefundSyncWorkflowImpl implements RefundSyncWorkflow {
    private final ActivityOptions options =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setHeartbeatTimeout(Duration.ofSeconds(30))
                    // disable retries for example to run faster
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .build();
    private final RefundStatusActivities activities =
            Workflow.newActivityStub(RefundStatusActivities.class, options);

    @Override
    public void syncRefundStatus() {
        Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(true).build();
        Saga saga = new Saga(sagaOptions);
        try {
            activities.invokeRefundSyncAPI();
            saga.addCompensation(activities::sendMessageToSlack);
        } catch (Exception e) {
            saga.compensate();
        }
    }
}
