package com.example.ace;

import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class LandingController {

    @GetMapping("/")
    public HashMap<String, String> list() {
        return new HashMap<String, String>() {{
            put("list", "GET https://ace.localhost/companies");
            put("get", "GET https://ace.localhost/companies/1");
        }};
    }

}
