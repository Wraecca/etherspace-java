# etherspace-java

A Retrofit-like Ethereum client for Android, Java, and Kotlin.

## Introduction

Etherspace is a type-safe Ethereum client to interact with Ethereum Smart Contract. 

For example, we have a `greeter` smart contract defined below: (see [ETHEREUM » Create a digital greeter](https://www.ethereum.org/greeter) for more details on Smart Contracts)
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

By defining a Smart Contract interface in Kotlin / Java:

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

Etherspace generates an implementation of `Greeter` interface. You can than use `greeter` to interact with the Greeter smart contract!
                                                               

```kotlin
// Kotlin
val etherSpace = EtherSpace.build {
    provider = "https://rinkeby.infura.io/" // Or your local node 
    credentials = Credentials(YOUR_PRIVATE_KEY_OR_WALLET)
}
var greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, Greeter::class.java)

val receipt = greeter.greeter("Hello World")
println(greeter.greet()) // Should be "Hello World"
```

```java
// Java
EtherSpace etherSpace = new EtherSpace.Builder()
        .provider("https://rinkeby.infura.io/") // Or your local node
        .credentials(new Credentials(YOUR_PRIVATE_KEY_OR_WALLET))
        .build();
Greeter greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, Greeter.class);

TransactionReceipt receipt = greeter.greeter("Hello World");
System.out.println(greeter.greet()); // Should be "Hello World"

```

## Smart Contract Declaration

Etherspace uses the annotations, methods, and parameters in the interface to define how to interact with Smart Contract. 

### Call/Send annotations

All the methods in the Smart Contract inteface need to be annotated either by `@cc.etherspace.Call` or `@cc.etherspace.Send`.
The method name is the function name of the underlying Smart Contract function. (or you can override it with `@Call.funcationName`)

#### @Call

Method annotated with @Call represent it's a constant(or View, Pure) function.
The type and order of input/output parameters must match those defined in the Smart Contract function.
Corresponding Solidity/Java Types are defined in [Solidity Data Types](#solidity-data-types).

#### @Send

Method annotated with @Send represent it's a function with transaction.
The type and order of input parameters must match those defined in the Smart Contract function.
The output parameter can be either `TransactionReceipt` or `String`(Transaction Hash). 

- TransactionReceipt

  Returns the transaction receipt of this transaction. This method will wait for the block completed.  

- String: transactionHash

  Returns the transaction hash without waiting for the block completed. This method will return right after the node accepted the transaction. (but not completed)

### Solidity Data Types

For types in Smart Contract, below is a list of their corresponding Java Types:

Solidity Type | Java Type
------------- | ---------
bool | `java.lang.Boolean`
int16 | `java.lang.Short`
int32 | `java.lang.Integer`
int64 | `java.lang.Long`
int / int256 | `java.math.BigInteger`
int8 ~ int248 (excluding int16,int32,int64) | `cc.etherspace.SolInt8` ~ `cc.etherspace.SolInt248`
uint16 | `unsigned.Ushort`
uint32 | `unsigned.Uint`
uint64 | `unsigned.Ulong`
uint / uint256 | `cc.etherspace.SolUint256`
uint8 ~ uint248 (excluding uint16,uint32,uint64) | `cc.etherspace.SolUint8` ~ `cc.etherspace.SolUint248`
address | `cc.etherspace.SolAddress`
byte / byte1 | `java.lang.Byte`
byte2 ~ byte32 | `cc.etherspace.SolBytes2` ~ `cc.etherspace.SolBytes32`
bytes | `byte[]` 
string | `java.lang.String`
dynamic array | `java.util.List<T>` (T must be one of the Java Types listed above.)
fixed size array | `cc.etherspace.SolArray1<T>` ~ `cc.etherspace.SolArray32<T>` (T must be one of the Java Types listed above.)   

### Solidity Multiple Return Values

Functions with multiple return values should use Pair, Triple, Tuples listed below: 

Number of Returns | Java Type
----------------- | ---------
2 | `kotlin.Pair`
3 | `kotlin.Triple`
4 | `cc.etherspace.Tuple4`
5 | `cc.etherspace.Tuple5`
6 | `cc.etherspace.Tuple6`
7 | `cc.etherspace.Tuple7`
8 | `cc.etherspace.Tuple8`
9 | `cc.etherspace.Tuple9`
10 | `cc.etherspace.Tuple10`

For example, a Smart Contract function with two return values:

```kotlin
// Kotlin
interface Greeter {
    @Throws(IOException::class)
    @Call
    fun greet(): Pair<String, String>
}
```

```java
// Java
public interface Greeter {
    @Call
    Pair<String, String> greet() throws IOException;
}
```

### Events

Event

```
event Modified(
        string indexed oldGreetingIdx, string indexed newGreetingIdx,
        string oldGreeting, string newGreeting);

```

```kotlin
// Kotlin
data class Modified @EventConstructor constructor(
    @Indexed(String::class) val oldGreetingIdx: SolBytes32,
    @Indexed(String::class) val newGreetingIdx: SolBytes32,
    val oldGreeting: String,
    val newGreeting: String)
    
```

```java
// Java
    class Modified {
        private SolBytes32 oldGreetingIdx;
        private SolBytes32 newGreetingIdx;
        private String oldGreeting;
        private String newGreeting;

        @EventConstructor
        public Modified(@Indexed(String.class) SolBytes32 oldGreetingIdx, @Indexed(String.class) SolBytes32 newGreetingIdx, String oldGreeting, String newGreeting) {
            this.oldGreetingIdx = oldGreetingIdx;
            this.newGreetingIdx = newGreetingIdx;
            this.oldGreeting = oldGreeting;
            this.newGreeting = newGreeting;
        }
        
        // getters/setters ... etc
    }
```

After Transaction Receipt received, you can list the received events from the transaction logs:

```kotlin
// Kotlin
val receipt = greeter.newGreeting("Hello World")
val events = receipt.listEvents(Modified::class.java)
```

```java
// Java
TransactionReceipt receipt = greeter.newGreeting("Hello World");
List<Event<Modified>> events = receipt.listEvents(Modified.class);
```

## Sync / Async

All method calls in Smart Contract interface are synchronized by default.
Etherspace supports Coroutine (Kotlin), CompletableFuture (Java), RxJava (Java) for asynchronized method calls.
Just make sure adding the corresponding `CallAdapter` in EtherSpace builder.

### CoroutineCallAdapter

```kotlin
interface CoroutineGreeter {
    @Throws(IOException::class)
    @Call
    fun greet(): Deferred<String>
}

val etherSpace = EtherSpace.build {
    provider = "https://rinkeby.infura.io/"
    credentials = Credentials(YOUR_PRIVATE_KEY_OR_WALLET)
    callAdapters += CoroutineCallAdapter()
}
greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, CoroutineGreeter::class.java)

runBlocking {
    println(greeter.greet().await()) // Should be Hello World
}
```

### CompletableFutureCallAdapter

```java
interface CompletableFutureCallAdapter {
    @Call
    CompletableFuture<String> greet() throws IOException;
}

EtherSpace etherSpace = new EtherSpace.Builder()
        .provider("https://rinkeby.infura.io/") // Or your local node
        .credentials(new Credentials(YOUR_PRIVATE_KEY_OR_WALLET))
        .addCallAdapter(new CompletableFutureCallAdapter<>())
        .build();
Greeter greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, CompletableFutureGreeter.class);

System.out.println(greeter.greet().join()); // Should be "Hello World"
```

### RxCallAdapter

```java
interface RxGreeter {
    @Call
    Observable<String> greet();
}

EtherSpace etherSpace = new EtherSpace.Builder()
        .provider("https://rinkeby.infura.io/") // Or your local node
        .credentials(new Credentials(YOUR_PRIVATE_KEY_OR_WALLET))
        .addCallAdapter(new RxCallAdapter<>())
        .build();
Greeter greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, RxGreeter.class);

greeter.greet().subscribe(System.out::println); // Should be "Hello World"
```

## Configuration

### Builder

Using `EtherSpace.Builder` to construct a new EtherSpace instance.

```java
EtherSpace etherSpace = new EtherSpace.Builder()
        .provider("https://rinkeby.infura.io/") // Or your local node
        .credentials(new Credentials(YOUR_PRIVATE_KEY_OR_WALLET))
        .addCallAdapter(new CompletableFutureCallAdapter<>())
        .client(OkHttpClient.Builder().build())
        .build();
```

- provider*: Ethereum node URL
- credentials: 

    Private Key or your wallet file. (format: [UTC JSON Keystore File](https://theethereum.wiki/w/index.php/Accounts,_Addresses,_Public_And_Private_Keys,_And_Tokens#UTC_JSON_Keystore_File))
    Can be null if you only want to make calls to constant functions.
    You Can also supply different credentials in [Options](#options).
    
- calladapters: 

    Etherspace supports different response types through `CallAdapter`. See [Sync / Async](#sync-/-async)

    Return Type | CallAdapter
    ----------- | -----------
    `kotlinx.coroutines.experimental.Deferred<T>` | `cc.etherspace.calladapter.CoroutineCallAdapter`
    `java.util.concurrent.CompletableFuture<T>` | `cc.etherspace.calladapter.CompletableFutureCallAdapter`
    `rx.Observable<T>` | `cc.etherspace.calladapter.RxCallAdapter`

- client: An `OkHttpClient`

### Options

An `Options` can be appended to the parameter list in Smart Contract methods.
Properties in `Options` will override predefined settings in runtime.
For example:

```java
// Java
public interface Greeter {
    @Call
    String greet() throws IOException;
}

EtherSpace etherSpace = new EtherSpace.Builder()
        .provider("https://rinkeby.infura.io/") // Or your local node
        .build();
Greeter greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, Greeter.class);

Options options = new Options(BigInteger.ZERO, BigInteger.valueOf(8_000_000), BigInteger.valueOf(22_000_000_000L), new Credentials(YOUR_PRIVATE_KEY_OR_WALLET));
System.out.println(greeter.greet(options)); // Should be "Hello World" with different Gas settings

```

### Gas

## Download