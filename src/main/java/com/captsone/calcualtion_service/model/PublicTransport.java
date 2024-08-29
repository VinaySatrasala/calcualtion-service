package com.captsone.calcualtion_service.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicTransport {
	
	private double flight_km;
	private double bus_km;
	private double train_km;
	private double emission;
}
