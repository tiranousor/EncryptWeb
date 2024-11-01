package com.example.WaterDelivery.providers;

import com.example.WaterDelivery.providers.WaterBottle;
import com.example.WaterDelivery.providers.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private Person person;

	@ManyToOne
	private WaterBottle waterBottle;

	private Integer quantity;

	@Transient
	private Double totalPrice;

	@Transient
	public Double totalOrderPrice;
}
