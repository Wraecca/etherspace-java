package cc.etherspace;

import org.junit.Before;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaGreeterTest {
    private JavaGreeter greeter;

    @Before
    public void setUp() {
        Credentials credentials = Credentials.create(
                ECKeyPair.create(
                        new BigInteger("77398679111088585283982189543320298238063257726010371587476264149399587362827")));
        EtherSpace etherSpace = new EtherSpace.Builder()
                .provider("https://rinkeby.infura.io/3teU4WimZ2pbdjPUDpPW")
                .credentials(credentials)
                .build();
        greeter = etherSpace.create(new SolAddress("0xa871c507184ecfaf947253e187826c1907e8dc7d"), JavaGreeter.class);
    }

    @Test
    public void greet() {
        String greet = greeter.greet();
        assertThat(greet).isEqualTo("Hello World");
    }

    @Test
    public void newGreeting() {
        String transactionHash = greeter.newGreeting("Hello World");
        assertThat(transactionHash.length()).isEqualTo(66);
    }
}
