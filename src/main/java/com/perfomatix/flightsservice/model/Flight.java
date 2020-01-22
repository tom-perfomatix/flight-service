package com.perfomatix.flightsservice.model;

public class Flight {

	String departure;
	String arrival;
	Double departureTime;
	Double arrivalTime;

	public Flight(String departure, String arrival, Double departureTime, Double arrivalTime) {
		super();
		this.departure = departure;
		this.arrival = arrival;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public Double getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Double departureTime) {
		this.departureTime = departureTime;
	}

	public Double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public boolean contains(String q) {
		String corpus = departure.concat(" " + arrival).toLowerCase();
		for (String key : q.split(" ")) {
			if (corpus.contains(key.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
