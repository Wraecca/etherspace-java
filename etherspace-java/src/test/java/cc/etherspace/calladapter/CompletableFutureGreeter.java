package cc.etherspace.calladapter;

import java.util.concurrent.CompletableFuture;

public interface CompletableFutureGreeter {
    CompletableFuture<String> newGreeting(String greeting);

    CompletableFuture<String> greet();
}
