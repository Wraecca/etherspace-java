package cc.etherspace.example;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import cc.etherspace.Call;
import cc.etherspace.Credentials;
import cc.etherspace.EtherSpace;
import cc.etherspace.Options;
import cc.etherspace.Send;
import cc.etherspace.TransactionHash;
import cc.etherspace.TransactionReceipt;
import cc.etherspace.calladapter.CompletableFutureCallAdapter;

public class CompletableFutureExample {
    public static void main(String[] args) throws IOException {
        System.out.println("Creating a new instance of Greeter");

        // Please fill in your private key or wallet file.
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(new Credentials("YOUR_PRIVATE_KEY_OR_WALLET"))
                .addCallAdapter(new CompletableFutureCallAdapter<>())
                .build();
        // The greeter smart contract has already been deployed to this address on rinkeby.
        Greeter greeter = etherSpace.create("0x7c7fd86443a8a0b249080cfab29f231c31806527", Greeter.class);

        System.out.println("Updating greeting to: Hello World");

        TransactionHash hash = greeter.newGreeting("Hello World").join();
        hash.<CompletableFuture<TransactionReceipt>>requestTransactionReceipt().join();
        System.out.println("Transaction returned with hash: " + hash.getHash());

        String greeting = greeter.greet().join();

        System.out.println("greeting is " + greeting + " now");

        System.out.println("Updating greeting with higher gas");

        Options options = new Options(BigInteger.ZERO, BigInteger.valueOf(5_300_000), BigInteger.valueOf(24_000_000_000L));
        hash = greeter.newGreeting("Hello World", options).join();
        hash.<CompletableFuture<TransactionReceipt>>requestTransactionReceipt().join();

        System.out.println("Transaction returned with hash: " + hash.getHash());
    }

    public interface Greeter {
        @Send
        CompletableFuture<TransactionHash> newGreeting(String greeting) throws IOException;

        @Send
        CompletableFuture<TransactionHash> newGreeting(String greeting, Options options) throws IOException;

        @Call
        CompletableFuture<String> greet();
    }
}
