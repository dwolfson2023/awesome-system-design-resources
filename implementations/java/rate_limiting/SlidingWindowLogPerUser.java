package rate_limiting;

import java.time.Instant;
import java.util.*;

public class SlidingWindowLogPerUser {
    private final long windowSizeInSeconds;   // Size of the sliding window in seconds
    private final long maxRequestsPerWindow;  // Maximum number of requests allowed in the window
    private final Queue<Long> requestLog;     // Log of request timestamps

    private final Map<String,Queue<Long>> userRequestLogs;

    public SlidingWindowLogPerUser(long windowSizeInSeconds, long maxRequestsPerWindow) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.requestLog = new LinkedList<>();
        this.userRequestLogs = new HashMap<>();
    }

    public synchronized boolean allowRequest() {
        long now = Instant.now().getEpochSecond();
        long windowStart = now - windowSizeInSeconds;

        // Remove timestamps that are outside of the current window
        while (!requestLog.isEmpty() && requestLog.peek() <= windowStart) {
            requestLog.poll();
        }

        if (requestLog.size() < maxRequestsPerWindow) {
            requestLog.offer(now);  // Log this request
            return true;            // Allow the request
        }
        return false;  // We've exceeded the limit for this window, deny the request
    }

    public synchronized boolean allowRequest(String userId) {
        long now = Instant.now().getEpochSecond();
        long windowStart = now - windowSizeInSeconds;
        if(!userRequestLogs.containsKey(userId)){
            userRequestLogs.put(userId, new LinkedList<>());
        }
        Queue<Long> requestLog = userRequestLogs.get(userId);
        // Remove timestamps that are outside of the current window
        while (!requestLog.isEmpty() && requestLog.peek() <= windowStart) {
            requestLog.poll();
        }

        if (requestLog.size() < maxRequestsPerWindow) {
            requestLog.offer(now);  // Log this request
            return true;            // Allow the request
        }
        return false;  // We've exceeded the limit for this window, deny the request
    }

    public static void main(String[] args) {
        SlidingWindowLogPerUser rateLimiter = new SlidingWindowLogPerUser(10, 5); // 5 requests per 10 seconds per user

        String userA = "userA";
        String userB = "userB";

        // Simulate requests from userA
        for (int i = 0; i < 7; i++) {
            boolean allowed = rateLimiter.allowRequest(userA);
            System.out.println("Request " + (i + 1) + " from " + userA + ": " + (allowed ? "Allowed" : "Denied"));
        }

        // Simulate requests from userB
        for (int i = 0; i < 3; i++) {
            boolean allowed = rateLimiter.allowRequest(userB);
            System.out.println("Request " + (i + 1) + " from " + userB + ": " + (allowed ? "Allowed" : "Denied"));
        }
    }
}
