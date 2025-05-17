package org.burza.rest_api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {
    @PostMapping("/download")


    @GetMapping("/hello")
    public String sayHello(@RequestParam(name = "name", defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    @PostMapping("/echo")
    public String echoMessage(@RequestBody String message) {
        return "You said: " + message;
    }
}