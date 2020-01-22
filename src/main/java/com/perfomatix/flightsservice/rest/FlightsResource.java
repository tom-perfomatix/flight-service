package com.perfomatix.flightsservice.rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/flights")
public class FlightsResource {
	final HttpRequest requestCheap = HttpRequest.newBuilder()
			.uri(URI.create("https://tokigames-challenge.herokuapp.com/api/flights/cheap")).build();

	final HttpRequest requestBusiness = HttpRequest.newBuilder()
			.uri(URI.create("https://tokigames-challenge.herokuapp.com/api/flights/business")).build();

	final HttpClient client = HttpClient.newHttpClient();
	final ObjectMapper objectMapper = new ObjectMapper();

	Function<String, List<Flight>> cheapFlightsList = json -> {
		List<Flight> flights = new ArrayList<>();
		try {
			objectMapper.readTree(json).path("data").elements()
					.forEachRemaining(e -> flights.add(
							new Flight(e.path("route").asText().split("-")[0], e.path("route").asText().split("-")[1],
									e.path("departure").asDouble(), e.path("arrival").asDouble())));
		} catch (JsonProcessingException e) {
		}
		return flights;
	};

	Function<String, List<Flight>> businessFlightsList = json -> {
		List<Flight> flights = new ArrayList<>();
		try {
			objectMapper.readTree(json).path("data").elements().forEachRemaining(
					e -> flights.add(new Flight(e.path("departure").asText(), e.path("arrival").asText(),
							e.path("departureTime").asDouble(), e.path("arrivalTime").asDouble())));
		} catch (JsonProcessingException e) {
		}
		return flights;
	};

	public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
		if (pageSize <= 0 || page <= 0) {
			throw new IllegalArgumentException("invalid page size: " + pageSize);
		}

		int fromIndex = (page - 1) * pageSize;
		if (sourceList == null || sourceList.size() < fromIndex) {
			return Collections.emptyList();
		}

		return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
	}

	@GetMapping(value = "/search")
	public Mono<List<Flight>> searchFlights(String q, String sortKey, String sortOrder, Integer page,
			Integer pageSize) {
		CompletableFuture<List<Flight>> cheapFlights = client
				.sendAsync(requestCheap, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.thenApply(cheapFlightsList);

		CompletableFuture<List<Flight>> businessFlights = client
				.sendAsync(requestBusiness, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.thenApply(businessFlightsList);

		CompletableFuture<List<Flight>> allFlights = cheapFlights.thenCombine(businessFlights, (cheap, business) -> {
			business.addAll(cheap);
			return business;
		});
		CompletableFuture<List<Flight>> filteredFlights = allFlights.thenApply(list -> {
			list = list.stream().filter(f -> f.contains(q)).collect(Collectors.toList());
			return list;
		});
		CompletableFuture<List<Flight>> sortedFights = filteredFlights.thenApply(list -> {
			list.sort(new FlightComparator(sortKey, sortOrder));
			return list;
		});

		CompletableFuture<List<Flight>> paginatedFights = sortedFights.thenApply(list -> {
			if (page != null && pageSize != null) {
				return getPage(list, page, pageSize);
			} else {
				return list;
			}
		});
		System.out.println(q);
		return Mono.fromFuture(paginatedFights);
	}

}