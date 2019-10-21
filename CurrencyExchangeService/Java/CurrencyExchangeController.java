package com.enver.microservices.currencyexchangeservice;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;

@RestController
public class CurrencyExchangeController {

    @Autowired
    private Environment environment;

    @Autowired
    private ExchangeValueRepository repository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public ExchangeValue retrieveExchangeValue(@PathVariable String from, @PathVariable String to){

        ExchangeValue exchangeValue = new ExchangeValue(1000L,from,to, BigDecimal.valueOf(65));

        //ExchangeValue exchangeValue = repository.findByFromAndTo(from,to);

        exchangeValue.setPort(Integer.parseInt(environment.getProperty("local.server.port")));

        logger.info("{}",exchangeValue);

        return exchangeValue;
    }

    @GetMapping("/currency-exchange-fault/from/{from}/to/{to}")
    @HystrixCommand(fallbackMethod="fallback")
    public ExchangeValue retrieveExchangeValueFault(@PathVariable String from, @PathVariable String to){
        throw new RuntimeException("not available");
    }

    public ExchangeValue fallback(String from, String to){
        ExchangeValue exchangeValue = new ExchangeValue(1000L,from,to, BigDecimal.valueOf(99));
        return exchangeValue;
    }
}
