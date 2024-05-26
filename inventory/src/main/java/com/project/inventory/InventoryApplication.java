package com.project.inventory;

import com.project.inventory.model.Inventory;
import com.project.inventory.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryApplication{

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}
	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository)
	{
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("Iphone 13");
			inventory.setQuantity(100);
			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("Iphone 14");
			inventory1.setQuantity(100);
			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);

		};
	}
}
