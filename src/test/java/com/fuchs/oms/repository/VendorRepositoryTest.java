package com.fuchs.oms.repository;

import com.fuchs.oms.model.Vendor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VendorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VendorRepository vendorRepository;

    @Test
    void shouldFindVendorByUsername() {
        Vendor vendor = new Vendor();
        vendor.setUsername("test-vendor");
        vendor.setPassword("hashedPassword");
        vendor.setName("Test Vendor");
        entityManager.persistAndFlush(vendor);

        Optional<Vendor> found = vendorRepository.findByUsername("test-vendor");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("test-vendor");
        assertThat(found.get().getName()).isEqualTo("Test Vendor");
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<Vendor> found = vendorRepository.findByUsername("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldSaveAndRetrieveVendor() {
        Vendor vendor = new Vendor();
        vendor.setUsername("save-test");
        vendor.setPassword("password");
        vendor.setName("Save Test Vendor");

        Vendor saved = vendorRepository.save(vendor);

        assertThat(saved.getId()).isNotNull();
        assertThat(vendorRepository.findById(saved.getId())).isPresent();
    }
}
