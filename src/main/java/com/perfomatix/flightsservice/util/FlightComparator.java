package com.perfomatix.flightsservice.util;

import java.util.Comparator;

import com.perfomatix.flightsservice.model.Flight;

public class FlightComparator implements Comparator<Flight> {

	String sortKey;
	String sortOrder;

	public FlightComparator(String sortKey, String sortOrder) {
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(Flight o1, Flight o2) {
		int compareVal = 0;
		switch (sortOrder) {
		case "asc":
			compareVal = compareFlights(1, o1, o2);
			break;
		case "desc":
			compareVal = compareFlights(-1, o1, o2);
			break;
		}
		return compareVal;
	}

	public int compareFlights(int order, Flight o1, Flight o2) {
		int compareVal = 0;
		switch (sortKey) {
		case "arrival":
			compareVal = order * o1.getArrival().compareTo(o2.getArrival());
			break;
		case "departure":
			compareVal = order * o1.getDeparture().compareTo(o2.getDeparture());
			break;
		case "departureTime":
			compareVal = order * (int) (o1.getDepartureTime() - o2.getDepartureTime());
			break;
		case "arrivalTime":
			compareVal = order * (int) (o1.getArrivalTime() - o2.getArrivalTime());
			break;
		}
		return compareVal;
	}
}
