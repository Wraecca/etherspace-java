package cc.etherspace.calladapter;

import cc.etherspace.*;
import org.junit.Before;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;

public class CompletableFutureGreeterTest {
    private CompletableFutureGreeter greeter;

    @Before
    public void setUp() {
        Credentials credentials = new Credentials("0xab1e199623aa5bb2c381c349b1734e31b5be08de0486ffab68e3af4853d9980b");
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(credentials)
                .addCallAdapter(new CompletableFutureCallAdapter<>())
                .build();
        greeter = etherSpace.create("0xa871c507184ecfaf947253e187826c1907e8dc7d", CompletableFutureGreeter.class);
    }

    @Test
    public void greet() throws Exception {
        String greet = greeter.greet().join();
        assertThat(greet).isEqualTo("Hello World");
    }

    @Test
    public void greet_wrongFunctionName() throws Exception {
        String greet = greeter.greet_wrongFunctionName().join();
        assertThat(greet).isNull();
    }

    @Test
    public void newGreeting() throws Exception {
        TransactionReceipt receipt = greeter.newGreeting("Hello World").join();
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

    @Test(expected = CompletionException.class)
    public void newGreeting_options() throws Exception {
        TransactionReceipt receipt = greeter.newGreeting("Hello World", new Options(BigInteger.ZERO, BigInteger.valueOf(44_000_000_000L))).join();
        assertThat(receipt.getTransactionHash().length()).isEqualTo(66);
    }
}
