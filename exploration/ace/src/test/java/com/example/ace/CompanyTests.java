package com.example.ace;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompanyTests {

    @DisplayName("Test toString")
    @Test
    void testToString() {
        final Company c =
            new Company(50,
                        "companyName",
                        "description",
                        "tagline",
                        "companyEmail",
                        "ein");
        Assertions.assertNotNull(c);
        Assertions.assertEquals(50, c.getId());
        Assertions.assertEquals("companyName", c.getCompanyName());
        Assertions.assertEquals("description", c.getDescription());
        Assertions.assertEquals("tagline", c.getTagline());
        Assertions.assertEquals("companyEmail", c.getCompanyEmail());
        Assertions.assertEquals("ein", c.getEin());

        final String expected = "Company {id: 50, companyName: companyName, description: description, tagline: tagline, companyEmail: companyEmail, ein: ein}";
        Assertions.assertEquals(expected, c.toString());
    }

}
