package com.captsone.calcualtion_service.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietaryHabits {
	private double meat_consumption;
	private double dairy_consumption;
	private double other_consumption;
	private double emission;
}
