package org.example.refundStatusSync;

import io.temporal.activity.ActivityInterface;

import java.io.IOException;

@ActivityInterface
public interface RefundStatusActivities {
    void sendMessageToSlack();

    void invokeRefundSyncAPI() throws IOException;
}
