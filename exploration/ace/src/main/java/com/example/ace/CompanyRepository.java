package com.example.ace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.springframework.core.io.ClassPathResource;


public class CompanyRepository {
    private static final String DATAFILEPATH = "data/companies.csv";
    private Map<Integer, Company> database;

    public CompanyRepository() {
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(Company.class).withHeader();

        try {
            final File file = new ClassPathResource(DATAFILEPATH).getFile();
            final MappingIterator<Company> it = mapper
                .readerFor(Company.class).with(schema).readValues(file);

            this.database = new HashMap<Integer, Company>();
            while (it.hasNextValue()) {
                final Company c = it.nextValue();
                this.database.put(c.getId(), c);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public int size() {
        return database.size();
    }

    public Company get(final int id) {
        return this.database.get(id);
    }

    public ArrayList<Company> list() {
        final ArrayList<Company> l = new ArrayList<Company>(this.database.values());

        Collections.sort(l, (a, b) -> Integer.compare(a.getId(), b.getId()));

        return l;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((database == null) ? 0 : database.hashCode());
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
        final CompanyRepository other = (CompanyRepository) obj;
        if (database == null) {
            if (other.database != null)
                return false;
        } else if (!database.equals(other.database))
            return false;
        return true;
    }

}
