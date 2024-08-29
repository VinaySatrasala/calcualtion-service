package com.captsone.calcualtion_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuelSources {

	private double lpg;
	private double firewood;
	private double emission;
}
