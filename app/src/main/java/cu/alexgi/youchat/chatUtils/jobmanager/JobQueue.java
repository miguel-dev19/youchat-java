package cu.alexgi.youchat.chatUtils.jobmanager;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

class JobQueue {

  private final Map<String, Job> activeGroupIds = new HashMap<>();
  private final LinkedList<Job>  jobQueue       = new LinkedList<>();

  synchronized void onRequirementStatusChanged() {
    notifyAll();
  }

  synchronized void add(Job job) {
    jobQueue.add(job);
    processJobAddition(job);
    notifyAll();
  }

  synchronized void addAll(List<Job> jobs) {
    jobQueue.addAll(jobs);

    for (Job job : jobs) {
      processJobAddition(job);
    }

    notifyAll();
  }

  private void processJobAddition(@NonNull Job job) {
    if (isJobActive(job) && isGroupIdAvailable(job)) {
      setGroupIdUnavailable(job);
    } else if (!isGroupIdAvailable(job)) {
      Job blockingJob = activeGroupIds.get(job.getGroupId());
      blockingJob.resetRunStats();
    }
  }

  synchronized void push(Job job) {
    jobQueue.addFirst(job);
  }

  synchronized Job getNext() {
    try {
      Job nextAvailableJob;

      while ((nextAvailableJob = getNextAvailableJob()) == null) {
        wait();
      }

      return nextAvailableJob;
    } catch (InterruptedException e) {
      throw new AssertionError(e);
    }
  }

  synchronized void setGroupIdAvailable(String groupId) {
    if (groupId != null) {
      activeGroupIds.remove(groupId);
      notifyAll();
    }
  }

  private Job getNextAvailableJob() {
    if (jobQueue.isEmpty()) return null;

    ListIterator<Job> iterator = jobQueue.listIterator();
    while (iterator.hasNext()) {
      Job job = iterator.next();

      if (job.isRequirementsMet() && isGroupIdAvailable(job)) {
        iterator.remove();
        setGroupIdUnavailable(job);
        return job;
      }
    }

    return null;
  }

  private boolean isJobActive(@NonNull Job job) {
    return job.getRetryUntil() > 0 && job.getRunIteration() > 0;
  }

  private boolean isGroupIdAvailable(@NonNull Job requester) {
    String groupId = requester.getGroupId();
    return groupId == null || !activeGroupIds.containsKey(groupId) || activeGroupIds.get(groupId).equals(requester);
  }

  private void setGroupIdUnavailable(@NonNull Job job) {
    String groupId = job.getGroupId();
    if (groupId != null) {
      activeGroupIds.put(groupId, job);
    }
  }
}
