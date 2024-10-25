package hello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloTest {

    @Test
    void sayHello() {
        var hello = new Hello();
        var message = hello.sayHello("Trainer");
        assertEquals("Hello Trainer!", message);
    }

}
