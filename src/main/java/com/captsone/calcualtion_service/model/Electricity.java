package com.captsone.calcualtion_service.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Electricity {

	private double kwh_used;
	private double emission;
}
