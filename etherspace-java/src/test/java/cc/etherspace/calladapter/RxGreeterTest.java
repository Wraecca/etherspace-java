package cc.etherspace.calladapter;

import cc.etherspace.*;
import org.junit.Before;
import org.junit.Test;
import org.web3j.utils.Numeric;
import rx.functions.Action1;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RxGreeterTest {
    private RxGreeter greeter;

    @Before
    public void setUp() {
        Credentials credentials = new Credentials("0xab1e199623aa5bb2c381c349b1734e31b5be08de0486ffab68e3af4853d9980b");
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(credentials)
                .addCallAdapter(new RxCallAdapter<>())
                .build();
        greeter = etherSpace.create("0xa871c507184ecfaf947253e187826c1907e8dc7d", RxGreeter.class);
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

        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        assertThat(subscriber.getOnNextEvents().get(0)).isNull();
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
        assertThat(receipt.getFrom()).isEqualTo("0x39759a3c0ada2d61b6ca8eb6afc8243075307ed3");
        assertThat(receipt.getTo()).isEqualTo("0xa871c507184ecfaf947253e187826c1907e8dc7d");
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
}
