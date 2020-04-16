package com.example.ace;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompanyControllerTests {
    private CompanyController cc;

    @BeforeEach
    void init() {
        cc = new CompanyController();
    }

    @DisplayName("Test list handler")
    @Test
    void testListHandler() {
        final List<Company> l = cc.list();
        Assertions.assertEquals(10, l.size());

        final List<Company> sorted =
            l.stream()
            .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
            .collect(Collectors.toList());
        Assertions.assertEquals(l, sorted);
    }

    @DisplayName("Test get handler")
    @Test
    void testGetHandler() {
        final Company c = cc.getById(3);
        final Company expected
            = new Company(3,
                          "Pfannerstill, Cremin and Spinka",
                          "Down-Sized Bi-Directional Projection",
                          "Engineer Frictionless Portals",
                          "dudley03@west.org",
                          "47-3670811");
        Assertions.assertEquals(expected, c);
    }
}
