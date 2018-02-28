# etherspace-java

A Retrofit-like Ethereum client for Android, Java, and Kotlin

## Introduction

Etherspace is a type-safe Ethereum client to interact with Smart Contract. 

For example, we have a greeter smart defined below:
```
contract greeter {
    /* Define variable greeting of the type string */
    string greeting;
    
    /* This runs when the contract is executed */
    function greeter(string _greeting) public {
        greeting = _greeting;
    }

    /* Main function */
    function greet() constant returns (string) {
        return greeting;
    }
}
```

By defining a Smart Contract interface in Java (Kotlin):

```kotlin
// Kotlin
interface Greeter {
    @Throws(IOException::class)
    @Send
    fun greeter(greeting: String): TransactionReceipt

    @Throws(IOException::class)
    @Call
    fun greet(): String
}
```

```java
// Java
public interface Greeter {
    @Send
    TransactionReceipt greeter(String greeting) throws IOException;
    
    @Call
    String greet() throws IOException;
}
```

Etherspace generates an implementation of `Greeter` interface.

```kotlin
// Kotlin
val etherSpace = EtherSpace.build {
    provider = "https://rinkeby.infura.io/" // Or your local node 
    credentials = Credentials(YOUR_PRIVATE_KEY_OR_WALLET)
}
var greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, Greeter::class.java)
```

```java
// Java
EtherSpace etherSpace = new EtherSpace.Builder()
        .provider("https://rinkeby.infura.io/") // Or your local node
        .credentials(new Credentials(YOUR_PRIVATE_KEY_OR_WALLET))
        .build();
Greeter greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, Greeter.class);
```

You can than use the `greeter` to interact with the Greeter smart contract!

Download