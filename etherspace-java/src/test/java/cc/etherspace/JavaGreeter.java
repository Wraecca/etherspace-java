package cc.etherspace;

public interface JavaGreeter {
    @Send
    String newGreeting(String greeting);

    @Call
    String greet();
}
