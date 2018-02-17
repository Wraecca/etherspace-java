package cc.etherspace.calladapter;

import cc.etherspace.Credentials;
import cc.etherspace.EtherSpace;
import cc.etherspace.SolAddress;
import org.junit.Before;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class CompletableFutureGreeterTest {
    private CompletableFutureGreeter greeter;

    @Before
    public void setUp() {
        Credentials credentials = new Credentials(Numeric.toHexStringWithPrefix(new BigInteger("77398679111088585283982189543320298238063257726010371587476264149399587362827")));
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/3teU4WimZ2pbdjPUDpPW")
                .credentials(credentials)
                .addCallAdapter(new CompletableFutureCallAdapter<>())
                .build();
        greeter = etherSpace.create(new SolAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d"), CompletableFutureGreeter.class);
    }

    @Test
    public void greet() {
        String greet = greeter.greet().join();
        assertThat(greet).isEqualTo("Hello World");
    }

    @Test
    public void newGreeting() {
        String transactionHash = greeter.newGreeting("Hello World").join();
        assertThat(transactionHash.length()).isEqualTo(66);
    }
}
