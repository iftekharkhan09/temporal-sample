package org.example.refundStatusSync;

import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;


public class RefundStatusSyncSaga {

  static final String TASK_QUEUE = "RefundSyncQueue1";

  @SuppressWarnings("CatchAndPrintStackTrace")
  public static void main(String[] args) {
     createWorkFlow();
    //createScheduleForRefundSyncAPI();
  }

  private static void createWorkFlow() {
    // gRPC stubs wrapper that talks to the local docker instance of temporal service.
    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    // client that can be used to start and signal workflows
    WorkflowClient client = WorkflowClient.newInstance(service);

    // worker factory that can be used to create workers for specific task queues
    WorkerFactory factory = WorkerFactory.newInstance(client);

    // Worker that listens on a task queue and hosts both workflow and activity implementations.
    Worker worker = factory.newWorker(TASK_QUEUE);

    // Workflows are stateful. So you need a type to create instances.
    worker.registerWorkflowImplementationTypes(RefundSyncWorkflowImpl.class);

    // Activities are stateless and thread safe. So a shared instance is used.
    RefundStatusActivities tripBookingActivities = new RefundStatusActivitiesImpl();
    worker.registerActivitiesImplementations(tripBookingActivities);

    // Start all workers created by this factory.
    factory.start();
    System.out.println("Worker started for task queue: " + TASK_QUEUE);

    // now we can start running instances of our saga - its state will be persisted
    WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE)
            .setWorkflowId("1")
            .setWorkflowIdReusePolicy( WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
            .setCronSchedule("* * * * *")
            .build();
    RefundSyncWorkflow refundSyncWorkflow = client.newWorkflowStub(RefundSyncWorkflow.class, options);
    refundSyncWorkflow.syncRefundStatus();
  }

  static void createScheduleForRefundSyncAPI() {
  }
}
