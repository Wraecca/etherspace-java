package cc.etherspace.calladapter;

import cc.etherspace.Call;
import cc.etherspace.Send;

import java.util.concurrent.CompletableFuture;

public interface CompletableFutureGreeter {
    @Send
    CompletableFuture<String> newGreeting(String greeting);

    @Call
    CompletableFuture<String> greet();
}
