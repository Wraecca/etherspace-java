package cc.etherspace.example;

import java.io.IOException;
import java.math.BigInteger;

import cc.etherspace.Call;
import cc.etherspace.WalletCredentials;
import cc.etherspace.EtherSpace;
import cc.etherspace.Options;
import cc.etherspace.Send;
import cc.etherspace.TransactionHash;
import cc.etherspace.TransactionReceipt;
import cc.etherspace.calladapter.RxJavaCallAdapter;
import rx.Observable;

public class RxJavaExample {
    public static void main(String[] args) throws IOException {
        System.out.println("Creating a new instance of Greeter");

        // Please fill in your private key or wallet file.
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(new WalletCredentials("YOUR_PRIVATE_KEY_OR_WALLET"))
                .addCallAdapter(new RxJavaCallAdapter<>())
                .build();
        // The greeter smart contract has already been deployed to this address on rinkeby.
        Greeter greeter = etherSpace.create("0x7c7fd86443a8a0b249080cfab29f231c31806527", Greeter.class);

        System.out.println("Updating greeting to: Hello World");

        greeter.newGreeting("Hello World")
                .flatMap(TransactionHash::<Observable<TransactionReceipt>>requestTransactionReceipt)
                .subscribe(receipt -> System.out.println("Transaction returned with hash: " + receipt.getTransactionHash()));

        greeter.greet().subscribe(greeting -> System.out.println("greeting is " + greeting + " now"));

        System.out.println("Updating greeting with higher gas");

        Options options = new Options(BigInteger.ZERO, BigInteger.valueOf(5_300_000), BigInteger.valueOf(24_000_000_000L));
        greeter.newGreeting("Hello World", options)
                .flatMap(TransactionHash::<Observable<TransactionReceipt>>requestTransactionReceipt)
                .subscribe(receipt -> System.out.println("Transaction returned with hash: " + receipt.getTransactionHash()));
    }

    public interface Greeter {
        @Send
        Observable<TransactionHash> newGreeting(String greeting) throws IOException;

        @Send
        Observable<TransactionHash> newGreeting(String greeting, Options options) throws IOException;

        @Call
        Observable<String> greet();
    }
}
