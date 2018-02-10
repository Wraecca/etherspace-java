package cc.etherspace.calladapter;

import cc.etherspace.View;

import java.util.concurrent.CompletableFuture;

public interface CompletableFutureGreeter {
    CompletableFuture<String> newGreeting(String greeting);

    @View
    CompletableFuture<String> greet();
}
