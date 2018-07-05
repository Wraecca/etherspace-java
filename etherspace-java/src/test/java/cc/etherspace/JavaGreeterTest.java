package cc.etherspace;

import org.junit.Before;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaGreeterTest {
    private JavaGreeter greeter;

    @Before
    public void setUp() {
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/")
                .credentials(new WalletCredentials(Tests.TEST_WALLET_KEY))
                .build();
        greeter = etherSpace.create(Tests.TEST_CONTRACT_ADDRESS, JavaGreeter.class);
    }

    @Test
    public void greet() {
        String greet = greeter.greet();
        assertThat(greet).isEqualTo("Hello World");
    }

    @Test(expected = IllegalArgumentException.class)
    public void greet_wrongFunctionName() {
        String greet = greeter.greet_wrongFunctionName();
        assertThat(greet).isEqualTo("Hello World");
    }

    @Test
    public void newGreeting() {
        TransactionReceipt receipt = greeter.newGreeting("Hello World");
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

    @Test(expected = IOException.class)
    public void newGreeting_options() throws Exception {
        greeter.newGreeting("Hello World", new Options(BigInteger.ZERO, BigInteger.valueOf(44_000_000_000L)));
    }

    @Test
    public void newGreeting_transactionHash() {
        TransactionHash hash = greeter.newGreeting_transactionHash("Hello World");
        assertThat(hash.getHash().length()).isEqualTo(66);

        TransactionReceipt receipt = hash.requestTransactionReceipt();
        assertThat(receipt.getBlockHash().length()).isEqualTo(66);
        assertThat(receipt.getTransactionHash().length()).isEqualTo(66);
        assertThat(receipt.getFrom()).isEqualTo(Tests.TEST_WALLET_ADDRESS);
        assertThat(receipt.getTo()).isEqualTo(Tests.TEST_CONTRACT_ADDRESS);
        assertThat(receipt.getLogs().size()).isGreaterThan(0);
    }
}
