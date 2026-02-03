package com.fuchs.oms.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VendorTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistVendorWithAllFields() {
        Vendor vendor = new Vendor();
        vendor.setUsername("test-vendor");
        vendor.setPassword("hashedPassword123");
        vendor.setName("Test Vendor Name");

        Vendor saved = entityManager.persistFlushFind(vendor);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("test-vendor");
        assertThat(saved.getPassword()).isEqualTo("hashedPassword123");
        assertThat(saved.getName()).isEqualTo("Test Vendor Name");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldEnforceUniqueUsername() {
        Vendor vendor1 = new Vendor();
        vendor1.setUsername("unique-vendor");
        vendor1.setPassword("password1");
        vendor1.setName("Vendor One");
        entityManager.persistAndFlush(vendor1);

        Vendor vendor2 = new Vendor();
        vendor2.setUsername("unique-vendor");
        vendor2.setPassword("password2");
        vendor2.setName("Vendor Two");

        org.junit.jupiter.api.Assertions.assertThrows(
            Exception.class,
            () -> entityManager.persistAndFlush(vendor2)
        );
    }

    @Test
    void shouldMapToVendorsTable() {
        Vendor vendor = new Vendor();
        assertThat(vendor.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("vendors");
    }
}
