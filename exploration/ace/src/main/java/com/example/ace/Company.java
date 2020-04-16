package com.example.ace;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "companyName", "description", "tagline", "companyEmail", "ein"})
public class Company {
    private int id;
    private String companyName;
    private String description;
    private String tagline;
    private String companyEmail;
    private String ein;

    public Company(final int id,
                   final String companyName,
                   final String description,
                   final String tagline,
                   final String companyEmail,
                   final String ein) {
        this.id = id;
        this.companyName = companyName;
        this.description = description;
        this.tagline = tagline;
        this.companyEmail = companyEmail;
        this.ein = ein;
    }

    /**
     * Constructor for a {@link Company}, for CsvMapper in {@link CompanyRepository} to use
     */
    public Company() {
    };

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(final String tagline) {
        this.tagline = tagline;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(final String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getEin() {
        return ein;
    }

    public void setEin(final String ein) {
        this.ein = ein;
    }

    @Override
    public String toString() {
        return String
            .format("Company {id: %d, companyName: %s, description: %s, tagline: %s, companyEmail: %s, ein: %s}",
                    id, companyName, description, tagline, companyEmail, ein);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((companyEmail == null) ? 0 : companyEmail.hashCode());
        result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((ein == null) ? 0 : ein.hashCode());
        result = prime * result + id;
        result = prime * result + ((tagline == null) ? 0 : tagline.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Company other = (Company) obj;
        if (companyEmail == null) {
            if (other.companyEmail != null)
                return false;
        } else if (!companyEmail.equals(other.companyEmail))
            return false;
        if (companyName == null) {
            if (other.companyName != null)
                return false;
        } else if (!companyName.equals(other.companyName))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (ein == null) {
            if (other.ein != null)
                return false;
        } else if (!ein.equals(other.ein))
            return false;
        if (id != other.id)
            return false;
        if (tagline == null) {
            if (other.tagline != null)
                return false;
        } else if (!tagline.equals(other.tagline))
            return false;
        return true;
    }



}
