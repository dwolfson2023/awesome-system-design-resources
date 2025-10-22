package rate_limiting;

import java.time.Instant;

public class TokenBucket {
    private final long capacity;        // Maximum number of tokens the bucket can hold
    private final double fillRate;      // Rate at which tokens are added to the bucket (tokens per second)
    private double tokens;              // Current number of tokens in the bucket
    private Instant lastRefillTimestamp; // Last time we refilled the bucket

    public TokenBucket(long capacity, double fillRate) {
        this.capacity = capacity;
        this.fillRate = fillRate;
        this.tokens = capacity;  // Start with a full bucket
        this.lastRefillTimestamp = Instant.now();
    }

    public synchronized boolean allowRequest(int tokens) {
        refill();  // First, add any new tokens based on elapsed time

        if (this.tokens < tokens) {
            return false;  // Not enough tokens, deny the request
        }

        this.tokens -= tokens;  // Consume the tokens
        return true;  // Allow the request
    }

    private void refill() {
        Instant now = Instant.now();
        // Calculate how many tokens to add based on the time elapsed
        double tokensToAdd = (now.toEpochMilli() - lastRefillTimestamp.toEpochMilli()) * fillRate / 1000.0;
        this.tokens = Math.min(capacity, this.tokens + tokensToAdd);  // Add tokens, but don't exceed capacity
        this.lastRefillTimestamp = now;
    }

    public static void main(String[] args) {
        TokenBucket tokenBucket = new TokenBucket(10, 1.0); // Capacity of 10 tokens, refill rate of 1 token per second

        // Simulate requests
        for (int i = 0; i < 150; i++) {
            boolean allowed = tokenBucket.allowRequest(1); // Each request consumes 1 token
            System.out.println("Request " + (i + 1) + ": " + (allowed ? "Allowed" : "Denied"));
            try {
                Thread.sleep(50); // Wait for 500 milliseconds between requests
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
