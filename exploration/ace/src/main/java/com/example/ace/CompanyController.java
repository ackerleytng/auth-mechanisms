package com.example.ace;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyRepository db;

    public CompanyController() {
        db = new CompanyRepository();
    }

    @GetMapping("/")
    @PreAuthorize("principal?.claims['resource_access']['ace']['roles'].contains('list')")
    public List<Company> list() {
        return db.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("principal?.claims['resource_access']['ace']['roles'].contains('detail')")
    public Company getById(@PathVariable("id") final Integer id) {
        return db.get(id);
    }
}
