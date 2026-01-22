package com.moyo.oms;

import com.moyo.oms.controller.HealthController;
import com.moyo.oms.repository.VendorRepository;
import com.moyo.oms.repository.ProductRepository;
import com.moyo.oms.repository.VendorProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MoyoOmsApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void healthControllerBeanExists() {
		assertThat(applicationContext.getBean(HealthController.class)).isNotNull();
	}

	@Test
	void repositoryBeansExist() {
		assertThat(applicationContext.getBean(VendorRepository.class)).isNotNull();
		assertThat(applicationContext.getBean(ProductRepository.class)).isNotNull();
		assertThat(applicationContext.getBean(VendorProductRepository.class)).isNotNull();
	}
}
