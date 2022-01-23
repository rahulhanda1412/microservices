package com.demo.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeProxy currencyExchangeProxy;
	
	//http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/5
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConverison calculateCurrencyConversion(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		Map<String,String> uriVariables = new HashMap<String,String>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConverison> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
				CurrencyConverison.class, uriVariables);
		CurrencyConverison currencyConverison = responseEntity.getBody();
		return new CurrencyConverison(responseEntity.getBody().getId(),
				responseEntity.getBody().getFrom(),to, currencyConverison.getConversionMultiple(),quantity,quantity.multiply(currencyConverison.getConversionMultiple()),responseEntity.getBody().getEnvironment());		
	}

	//http://localhost:8100/currency-conversion-feign/from/USD/to/INR/quantity/5
	//http://localhost:8675/currency-conversion-feign/from/USD/to/INR/quantity/5
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConverison calculateCurrencyConversionfeign(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		CurrencyConverison currencyConverison = currencyExchangeProxy.retrieveExchange(from, to);
		return new CurrencyConverison(currencyConverison.getId(),
				currencyConverison.getFrom(),to, currencyConverison.getConversionMultiple(),quantity,quantity.multiply(currencyConverison.getConversionMultiple()),currencyConverison.getEnvironment());
	}
	
	@GetMapping("/currency-conversion")
	public String calculateCurrencyConversionfeign() {
		return "currencyConverison";
		
	}

}
