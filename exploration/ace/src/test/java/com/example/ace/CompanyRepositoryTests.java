package com.example.ace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CompanyRepositoryTests {

    @DisplayName("Test loading data")
    @Test
    void testLoadingData() {
        final CompanyRepository cr = new CompanyRepository();
        Assertions.assertNotNull(cr);
        Assertions.assertEquals(10, cr.size());
    }

    @DisplayName("Test get by id")
    @Test
    void testGetById() {
        final CompanyRepository cr = new CompanyRepository();
        final Company c = cr.get(1);

        Assertions.assertNotNull(c);
        Assertions.assertEquals(1, c.getId());
        Assertions.assertEquals("O'Conner-Gleichner", c.getCompanyName());
        Assertions.assertEquals("Stand-Alone Well-Modulated Core", c.getDescription());
        Assertions.assertEquals("Integrate Transparent Functionalities", c.getTagline());
        Assertions.assertEquals("jenkins.juvenal@lesch.net", c.getCompanyEmail());
        Assertions.assertEquals("80-9295526", c.getEin());
    }

    @DisplayName("Test list")
    @Test
    void testList() {
        final CompanyRepository cr = new CompanyRepository();
        final ArrayList<Company> cs = cr.list();

        Assertions.assertEquals(10, cs.size());
        final Company expected =
            new Company(7,
                        "Klein, Green and Collins",
                        "Profit-Focused Static Leverage",
                        "Integrate Customized Supply-Chains",
                        "ngrimes@schamberger.com",
                        "90-7339275");
        Assertions.assertEquals(expected, cs.get(6));

        // Test that list is sorted
        final List<Integer> ids = cs.stream().map(c -> c.getId()).collect(Collectors.toList());
        final ArrayList<Integer> sorted = new ArrayList<Integer>(ids);
        Collections.sort(sorted);

        Assertions.assertEquals(sorted, ids);
    }

}
