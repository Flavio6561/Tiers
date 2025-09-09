package com.tiers;

import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;

import java.util.concurrent.*;

public class PlayerProfileQueue {
    private static final ConcurrentLinkedDeque<PlayerProfile> queue = new ConcurrentLinkedDeque<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static PlayerProfile currentProfile = null;

    static {
        scheduler.scheduleAtFixedRate(PlayerProfileQueue::processNext, 0, 50, TimeUnit.MILLISECONDS);
    }

    public static void enqueue(PlayerProfile playerProfile) {
        queue.add(playerProfile);
    }

    private static void processNext() {
        if (currentProfile != null && currentProfile.status == Status.SEARCHING)
            return;

        currentProfile = queue.poll();
        if (currentProfile != null)
            currentProfile.buildRequest();
    }

    public static void putFirstInQueue(PlayerProfile playerProfile) {
        if (currentProfile == playerProfile)
            return;

        queue.remove(playerProfile);
        queue.addFirst(playerProfile);
    }

    public static void changeToFirstInQueue(PlayerProfile playerProfile) {
        if (currentProfile == playerProfile)
            return;

        if (queue.contains(playerProfile)) {
            queue.remove(playerProfile);
            queue.addFirst(playerProfile);
        }
    }

    public static void clearQueue() {
        queue.clear();
        currentProfile = null;
    }

    public static void removeFromQueue(PlayerProfile playerProfile) {
        if (currentProfile == playerProfile)
            currentProfile = null;

        queue.remove(playerProfile);
    }
}