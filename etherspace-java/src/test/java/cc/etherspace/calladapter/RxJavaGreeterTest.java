package cc.etherspace.calladapter;

import org.junit.Before;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import cc.etherspace.WalletCredentials;
import cc.etherspace.EtherSpace;
import cc.etherspace.Event;
import cc.etherspace.JavaGreeter;
import cc.etherspace.Options;
import cc.etherspace.SolBytes32;
import cc.etherspace.Tests;
import cc.etherspace.TransactionHash;
import cc.etherspace.TransactionReceipt;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

public class RxJavaGreeterTest {
    private RxJavaGreeter greeter;

    @Before
    public void setUp() {
        WalletCredentials credentials = new WalletCredentials(Tests.TEST_WALLET_KEY);
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(credentials)
                .addCallAdapter(new RxJavaCallAdapter<>())
                .build();
        greeter = etherSpace.create(Tests.TEST_CONTRACT_ADDRESS, RxJavaGreeter.class);
    }

    @Test
    public void greet() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        greeter.greet().subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        assertThat(subscriber.getOnNextEvents().get(0)).isEqualTo("Hello World");

        greeter.greet().subscribe(System.out::println);
    }

    @Test
    public void greet_wrongFunctionName() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        greeter.greet_wrongFunctionName().subscribe(subscriber);
        subscriber.assertError(IllegalArgumentException.class);
    }

    @Test
    public void newGreeting() {
        TestSubscriber<TransactionReceipt> subscriber = new TestSubscriber<>();
        greeter.newGreeting("Hello World").subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        TransactionReceipt receipt = subscriber.getOnNextEvents().get(0);
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

    @Test
    public void newGreeting_options() {
        TestSubscriber<TransactionReceipt> subscriber = new TestSubscriber<>();
        greeter.newGreeting("Hello World", new Options(BigInteger.ZERO, BigInteger.valueOf(44_000_000_000L))).subscribe(subscriber);
        subscriber.assertError(IOException.class);
    }


    @Test
    public void newGreeting_transactionHash() {
        TestSubscriber<TransactionHash> s1 = new TestSubscriber<>();
        greeter.newGreeting_transactionHash("Hello World").subscribe(s1);

        s1.assertCompleted();
        s1.assertNoErrors();
        s1.assertValueCount(1);
        TransactionHash hash = s1.getOnNextEvents().get(0);
        assertThat(hash.getHash().length()).isEqualTo(66);

        TestSubscriber<TransactionReceipt> s2 = new TestSubscriber<>();
        hash.<Observable<TransactionReceipt>>requestTransactionReceipt().subscribe(s2);

        s2.assertCompleted();
        s2.assertNoErrors();
        s2.assertValueCount(1);
        TransactionReceipt receipt = s2.getOnNextEvents().get(0);
        assertThat(receipt.getBlockHash().length()).isEqualTo(66);
        assertThat(receipt.getTransactionHash().length()).isEqualTo(66);
        assertThat(receipt.getFrom()).isEqualTo(Tests.TEST_WALLET_ADDRESS);
        assertThat(receipt.getTo()).isEqualTo(Tests.TEST_CONTRACT_ADDRESS);
        assertThat(receipt.getLogs().size()).isGreaterThan(0);
    }
}
