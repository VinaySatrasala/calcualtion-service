package com.captsone.calcualtion_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.captsone.calcualtion_service.external.CarbonEmissionClient;
import com.captsone.calcualtion_service.model.DietaryHabits;
import com.captsone.calcualtion_service.model.Electricity;
import com.captsone.calcualtion_service.model.FuelSources;
import com.captsone.calcualtion_service.model.PrivateTransport;
import com.captsone.calcualtion_service.model.PublicTransport;
import com.captsone.calcualtion_service.model.Waste;
import com.captsone.calcualtion_service.model.Water;

/**
 * CalcController handles API requests related to the calculation of carbon emissions
 * from various sources such as electricity, water usage, waste management, fuel sources,
 * dietary habits, and transportation.
 */
@RestController
@RequestMapping("/api/v1/calculate")
public class CalcController {

    @Autowired
    private RestTemplate restTemplate;

    //for gateway testing
    @GetMapping
    public String test() {
        return "Calculation Service is up and running";
    }
    
    /**
     * Calculates the carbon emissions from electricity usage.
     *
     * @param electricity The electricity usage details.
     * @return A ResponseEntity containing the electricity details with calculated emissions.
     */
    @PostMapping("/electricity")
    public ResponseEntity<Electricity> electricityEmission(@RequestBody Electricity electricity) {
        double emissionFactor = 0.92;  // The emission factor in kg CO2e per kWh

        // Emission = kwh * emissionFactor;
        double emission = electricity.getKwh_used() * emissionFactor;

        electricity.setEmission(emission);

        return ResponseEntity.ok().body(electricity);
    }

    /**
     * Calculates the carbon emissions from water usage.
     *
     * @param waterUsed The water usage details.
     * @return A ResponseEntity containing the water usage details with calculated emissions.
     */
    @PostMapping("/water")
    public ResponseEntity<Water> waterEmission(@RequestBody Water waterUsed) {

        double emissionFactor = 0.0003;  // The emission factor in kg CO2e per liter

        // Emission = liters of water used * emissionFactor;
        double emission = waterUsed.getLitres_used() * emissionFactor;

        waterUsed.setEmission(emission);

        return ResponseEntity.ok().body(waterUsed);
    }

    /**
     * Calculates the carbon emissions from waste management.
     *
     * @param waste The waste details, including recyclable and non-recyclable waste.
     * @return A ResponseEntity containing the waste details with calculated emissions.
     */
    @PostMapping("/waste")
    public ResponseEntity<Waste> wasteEmission(@RequestBody Waste waste) {

        double recyclableEmissionFactor = 0.02;  // Emission factor for recycling in kg CO2e per kg
        double nonRecyclableEmissionFactor = 0.5;  // Emission factor for landfill in kg CO2e per kg

        double recyclableEmissions = recyclableEmissionFactor * waste.getRecyclable_waste();
        double nonRecyclableEmissions = nonRecyclableEmissionFactor * waste.getNon_recyclable_waste();

        waste.setEmission(nonRecyclableEmissions + recyclableEmissions);

        return ResponseEntity.ok().body(waste);
    }

    /**
     * Calculates the carbon emissions from fuel sources such as LPG and firewood.
     *
     * @param fuel The fuel details, including LPG and firewood usage.
     * @return A ResponseEntity containing the fuel details with calculated emissions.
     */
    @PostMapping("/fuel_sources")
    public ResponseEntity<FuelSources> fuelEmission(@RequestBody FuelSources fuel) {

        double lpgEmissionFactor = 2.98;  // Emission factor for LPG in kg CO2e per liter
        double firewoodEmissionFactor = 1.9;  // Emission factor for firewood in kg CO2e per kg

        double lpgEmissions = fuel.getLpg() * lpgEmissionFactor;
        double firewoodEmissions = fuel.getFirewood() * firewoodEmissionFactor;

        fuel.setEmission(firewoodEmissions + lpgEmissions);
        return ResponseEntity.ok().body(fuel);
    }

    /**
     * Calculates the carbon emissions based on dietary habits.
     *
     * @param diet The dietary habits, including meat, dairy, and other food consumption.
     * @return A ResponseEntity containing the dietary habits with calculated emissions.
     */
    @PostMapping("/dietary_habits")
    public ResponseEntity<DietaryHabits> dietEmission(@RequestBody DietaryHabits diet) {

        final double MEAT_EMISSION_FACTOR = 27.0;  // kg CO2e per kg of meat
        final double DAIRY_EMISSION_FACTOR = 3.2;  // kg CO2e per kg of dairy
        final double OTHER_EMISSION_FACTOR = 2.0;  // kg CO2e per kg of other food

        double meatEmissions = diet.getMeat_consumption() * MEAT_EMISSION_FACTOR;
        double dairyEmissions = diet.getDairy_consumption() * DAIRY_EMISSION_FACTOR;
        double otherEmissions = diet.getOther_consumption() * OTHER_EMISSION_FACTOR;
        diet.setEmission(meatEmissions + dairyEmissions + otherEmissions);
        return ResponseEntity.ok().body(diet);
    }

    /**
     * Calculates the carbon emissions from public transportation, including flights, buses, and trains.
     *
     * @param map A map containing the travel details for public transport.
     * @return A ResponseEntity containing the public transport details with calculated emissions.
     */
    @PostMapping("/public_transport")
    public ResponseEntity<PublicTransport> publicTransportEmission(@RequestBody Map<String,Object> map) {
        final double TRAIN_EMISSION_FACTOR = 0.041;  // kg CO2e per passenger per km
        final double BUS_EMISSION_FACTOR = 0.089;    // kg CO2e per passenger per km

        PublicTransport pt = new PublicTransport();

        double emission = 0.0;
        if(map.containsKey("flight")){
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Map<String,Object> flight =(Map) map.get("flight");
            String from = flight.get("from").toString();
            String to = flight.get("to").toString();
            CarbonEmissionClient client = new CarbonEmissionClient(from,to);
            double[] res = client.sendFlightEmissionRequest();
            emission +=  res[0];
            pt.setFlight_km(res[1]);
        }

        if(map.containsKey("bus")){
        	
            double kmTravelled = Double.parseDouble(map.get("bus").toString());      
            pt.setBus_km(kmTravelled);
            emission += kmTravelled * BUS_EMISSION_FACTOR;
            
        }

        if(map.containsKey("train")){
            double kmTravelled = Double.parseDouble(map.get("train").toString());
            pt.setTrain_km(kmTravelled);
            emission += kmTravelled * TRAIN_EMISSION_FACTOR;
        }

        pt.setEmission(emission);

        return ResponseEntity.ok().body(pt);
    }

    /**
     * Calculates the carbon emissions from private transportation based on fuel type, vehicle efficiency, and distance traveled.
     *
     * @param map A map containing the private transport details, including fuel type, efficiency, and distance traveled.
     * @return A ResponseEntity containing the private transport details with calculated emissions.
     */
    @PostMapping("/private_transport")
    public ResponseEntity<?> privateTransportEmission(@RequestBody Map<String,Object> map) {
	        String fuel_type = map.get("fuel_type").toString();
	        double vehicla_efficiency = Double.parseDouble(map.get("efficiency").toString()) ;
	        double distance_travelled = Double.parseDouble(map.get("travelled").toString()) ; 
	
	        double emissionFactor;
	        switch (fuel_type) {
	            case "Diesel":
	                emissionFactor = 2.68;  // kg CO2e per liter for Diesel
	                break;
	            case "Natural Gas":
	                emissionFactor = 1.93;  // kg CO2e per liter for Natural Gas
	                break;
	            default:
	                emissionFactor = 2.31;  // kg CO2e per liter for Petrol
	                break;
	        }
	        double emission = (distance_travelled / vehicla_efficiency) * emissionFactor;
	
	        PrivateTransport pt = new PrivateTransport();
	        pt.setDistance(distance_travelled);
	        pt.setEmission(emission);
	
	        return ResponseEntity.ok().body(pt);

		
    }
    
    
}
