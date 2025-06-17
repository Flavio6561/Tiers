package com.tiers;

import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;

import java.util.concurrent.*;

public class PlayerProfileQueue {
    private static final ConcurrentLinkedDeque<PlayerProfile> queue = new ConcurrentLinkedDeque<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static PlayerProfile currentProfile = null;

    static {
        scheduler.scheduleAtFixedRate(PlayerProfileQueue::processNext, 0, 100, TimeUnit.MILLISECONDS);
    }

    public static void enqueue(PlayerProfile profile) {
        queue.add(profile);
    }

    private static void processNext() {
        if (currentProfile != null && currentProfile.status == Status.SEARCHING)
            return;

        currentProfile = queue.poll();
        if (currentProfile != null)
            currentProfile.buildRequest(currentProfile.name);
    }

    public static void putFirstInQueue(PlayerProfile profile) {
        if (currentProfile == profile)
            return;

        queue.remove(profile);
        queue.addFirst(profile);
    }

    public static void changeToFirstInQueue(PlayerProfile profile) {
        if (currentProfile == profile)
            return;

        if (queue.contains(profile)) {
            queue.remove(profile);
            queue.addFirst(profile);
        }
    }

    public static void clearQueue() {
        queue.clear();
        currentProfile = null;
    }
}