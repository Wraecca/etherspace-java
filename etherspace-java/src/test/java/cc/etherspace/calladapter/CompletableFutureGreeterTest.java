package cc.etherspace.calladapter;

import org.junit.Before;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cc.etherspace.WalletCredentials;
import cc.etherspace.EtherSpace;
import cc.etherspace.Event;
import cc.etherspace.JavaGreeter;
import cc.etherspace.Options;
import cc.etherspace.SolBytes32;
import cc.etherspace.Tests;
import cc.etherspace.TransactionHash;
import cc.etherspace.TransactionReceipt;

import static org.assertj.core.api.Assertions.assertThat;

public class CompletableFutureGreeterTest {
    private CompletableFutureGreeter greeter;

    @Before
    public void setUp() {
        WalletCredentials credentials = new WalletCredentials(Tests.TEST_WALLET_KEY);
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(credentials)
                .addCallAdapter(new CompletableFutureCallAdapter<>())
                .build();
        greeter = etherSpace.create(Tests.TEST_CONTRACT_ADDRESS, CompletableFutureGreeter.class);
    }

    @Test
    public void greet() throws Exception {
        String greet = greeter.greet().join();
        assertThat(greet).isEqualTo("Hello World");
    }

    @Test(expected = CompletionException.class)
    public void greet_wrongFunctionName() throws Exception {
        String greet = greeter.greet_wrongFunctionName().join();
        assertThat(greet).isEqualTo("Hello World");
    }

    @Test
    public void newGreeting() throws Exception {
        TransactionReceipt receipt = greeter.newGreeting("Hello World").join();
        assertThat(receipt.getBlockHash().length()).isEqualTo(66);
        assertThat(receipt.getTransactionHash().length()).isEqualTo(66);
        assertThat(receipt.getFrom()).isEqualTo(Tests.TEST_WALLET_ADDRESS);
        assertThat(receipt.getTo()).isEqualTo(Tests.TEST_CONTRACT_ADDRESS);
        assertThat(receipt.getLogs().size()).isGreaterThan(0);

        List<Event<JavaGreeter.Modified>> events = receipt.listEvents(JavaGreeter.Modified.class);
        assertThat(events.size()).isEqualTo(1);
        Event<JavaGreeter.Modified> event = events.get(0);
        assertThat(event.getEvent()).isEqualTo("Modified");
        assertThat(event.getReturnValue().getOldGreeting()).isEqualTo("Hello World");
        assertThat(event.getReturnValue().getNewGreeting()).isEqualTo("Hello World");
        assertThat(event.getReturnValue().getOldGreetingIdx()).isEqualTo(new SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")));
        assertThat(event.getReturnValue().getNewGreetingIdx()).isEqualTo(new SolBytes32(Numeric.hexStringToByteArray("0x592fa743889fc7f92ac2a37bb1f5ba1daf2a5c84741ca0e0061d243a2e6707ba")));
    }

    @Test(expected = CompletionException.class)
    public void newGreeting_options() throws Exception {
        TransactionReceipt receipt = greeter.newGreeting("Hello World", new Options(BigInteger.ZERO, BigInteger.valueOf(44_000_000_000L))).join();
        assertThat(receipt.getTransactionHash().length()).isEqualTo(66);
    }

    @Test
    public void newGreeting_transactionHash() throws Exception {
        TransactionHash hash = greeter.newGreeting_transactionHash("Hello World").join();
        assertThat(hash.getHash().length()).isEqualTo(66);
        TransactionReceipt receipt = hash.<CompletableFuture<TransactionReceipt>>requestTransactionReceipt().join();

        assertThat(receipt.getBlockHash().length()).isEqualTo(66);
        assertThat(receipt.getTransactionHash().length()).isEqualTo(66);
        assertThat(receipt.getFrom()).isEqualTo(Tests.TEST_WALLET_ADDRESS);
        assertThat(receipt.getTo()).isEqualTo(Tests.TEST_CONTRACT_ADDRESS);
        assertThat(receipt.getLogs().size()).isGreaterThan(0);
    }
}
