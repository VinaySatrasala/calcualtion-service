package com.captsone.calcualtion_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Waste {

	private double recyclable_waste;
	private double non_recyclable_waste;
	private double emission;
}
