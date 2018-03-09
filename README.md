# Android native wallet proof of concept

![wallet transaction flow](https://raw.githubusercontent.com/Wraecca/etherspace-java/wallet/doc/wallet.png)

This branch includes a proof-of-concept implementation of Android native wallet.
The transaction created in DApp is passed to the wallet, and user can sign the transaction inside the wallet.
The signed transaction is then passed back to the DApp and submitted to Ethereum node to complete the transaction.

## Install and Run

- Please clones the repository and opens the project with Android Studio.
- Deploys `etherspace-android-wallet` on emulator or Android phones with Android SDK 26.
- Deploys and launchs `etherspace-android-example` on emulator or Android phones with Android SDK 26.
- Like the screenshots above, clicks on the read/update to interact with the Smart Contract. 

# etherspace-java

A Retrofit-like Ethereum client for Android, Java, and Kotlin.

[![Release](https://jitpack.io/v/cc.etherspace.etherspace-java/etherspace-java.svg?style=flat-square)](https://jitpack.io/#cc.etherspace.etherspace-java/etherspace-java)
[![Build Status](https://travis-ci.org/Wraecca/etherspace-java.svg?branch=master)](https://travis-ci.org/Wraecca/etherspace-java)

## Introduction

Etherspace is a type-safe Ethereum client to interact with Ethereum Smart Contract. 

For example, to access `greeter` smart contract from [ETHEREUM » Create a digital greeter](https://www.ethereum.org/greeter) (Slightly modified because we need a setter):

```javascript
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

    /* Update greeting */
    function newGreeting(string _greeting) public returns (string) {
        greeting = _greeting;
        return greeting;
    }
}
```

By defining a Smart Contract interface in Kotlin / Java: (see: [Smart Contract Interface](https://github.com/Wraecca/etherspace-java/wiki/Smart-Contract-Interface))

```kotlin
// Kotlin
interface Greeter {
    @Throws(IOException::class)
    @Send
    fun newGreeting(greeting: String): TransactionReceipt

    @Throws(IOException::class)
    @Call
    fun greet(): String
}
```

```java
// Java
public interface Greeter {
    @Send
    TransactionReceipt newGreeting(String greeting) throws IOException;
    
    @Call
    String greet() throws IOException;
}
```

Etherspace generates an implementation of `Greeter` interface. 

You can than use `greeter` to interact with Smart Contract on Ethereum!
                                                               
```kotlin
// Kotlin
val etherSpace = EtherSpace.build {
    provider = "https://rinkeby.infura.io/" // Or your local node 
    credentials = Credentials(YOUR_PRIVATE_KEY_OR_WALLET)
}
var greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, Greeter::class.java)

val receipt = greeter.newGreeting("Hello World")
println(greeter.greet()) // Should be "Hello World"
```

```java
// Java
EtherSpace etherSpace = new EtherSpace.Builder()
        .provider("https://rinkeby.infura.io/") // Or your local node
        .credentials(new Credentials(YOUR_PRIVATE_KEY_OR_WALLET))
        .build();
Greeter greeter = etherSpace.create(SMART_CONTRACT_ADDRESS, Greeter.class);

TransactionReceipt receipt = greeter.newGreeting("Hello World");
System.out.println(greeter.greet()); // Should be "Hello World"

```

## Example Apps

- Java / Kotlin: [etherspace-java-example](https://github.com/Wraecca/etherspace-java/tree/master/etherspace-java-example)
- Android: [etherspace-android-example](https://github.com/Wraecca/etherspace-java/tree/master/etherspace-android-example)

## Declaring Smart Contract Interfaces

- [Smart Contract Interface](https://github.com/Wraecca/etherspace-java/wiki/Smart-Contract-Interface)

## Configuration

- [Configuration](https://github.com/Wraecca/etherspace-java/wiki/Configuration)

## Download

[![Release](https://jitpack.io/v/cc.etherspace.etherspace-java/Repo.svg?style=flat-square)](https://jitpack.io/#cc.etherspace.etherspace-java/Repo)

- Gradle
  ```gradle
  repositories {
    ...
    maven { url 'https://jitpack.io' } // should be the last entry
  }

  dependencies {
    ...
    // JDK 8
    compile 'cc.etherspace.etherspace-java:etherspace-java:{version}'
    // Android
    implementation 'cc.etherspace.etherspace-java:etherspace-android:{version}'
  }
  ```

- Maven

  ```xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

  <!-- JDK 8 -->
  <dependency>
    <groupId>cc.etherspace.etherspace-java</groupId>
    <artifactId>etherspace-java</artifactId>
    <version>{version}</version>
  </dependency>

  <!-- Android -->
  <dependency>
    <groupId>cc.etherspace.etherspace-java</groupId>
    <artifactId>etherspace-android</artifactId>
    <version>{version}</version>
  </dependency>
  ```
  
## Bug reports & feature requests

Please submit [issues](https://github.com/Wraecca/etherspace-java/issues) or [pull requests](https://github.com/Wraecca/etherspace-java/pulls) for bugs and features, or contact me at [tempo@zaoo.com](mailto:tempo@zaoo.com).
Any feedback is welcome!

## Credits

- Etherspace is built by [Kotlin](https://kotlinlang.org/).
- Built on top of [web3j](https://github.com/web3j/web3j).
- Inspired by [Retrofit](http://square.github.io/retrofit/).
- Included libraries:
  - web3j: https://github.com/web3j/web3j
  - kotlin-unsigned: https://github.com/kotlin-graphics/kotlin-unsigned
  - jackson: https://github.com/FasterXML/jackson
  - guava: https://github.com/google/guava
