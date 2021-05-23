package com.geekbang.demo.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TempController {

    @GetMapping("user")
    public static String userHttp(@RequestParam(required = false) Integer instanceFrom) {
        if (instanceFrom != null) {
            return "Welcome to User module" + ", this is instance " + instanceFrom;
        } else {
            return "Welcome to User module";
        }
    }

    @GetMapping("log")
    public static String logHttp(@RequestParam(required = false) Integer instanceFrom) {
        if (instanceFrom != null) {
            return "Welcome to Log module" + ", this is instance " + instanceFrom;
        } else {
            return "Welcome to Log module";
        }
    }

    @GetMapping("data")
    public static String dataHttp(@RequestParam(required = false) Integer instanceFrom) {
        if (instanceFrom != null) {
            return "Welcome to Data module" + ", this is instance " + instanceFrom;
        } else {
            return "Welcome to Data module";
        }
    }

    @GetMapping("error")
    public static String errorHttp() {
        return "This is a error request";
    }
}
