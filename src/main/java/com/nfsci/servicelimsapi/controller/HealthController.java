package com.nfsci.servicelimsapi.controller;

import io.swagger.api.HealthApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * service-ngs-api
 */

@RestController
@RequestMapping("/health")
@CrossOrigin
public class HealthController implements HealthApi {

    private static final Logger LOG = LoggerFactory.getLogger(HealthController.class);

    @Override
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<ResponseEntity<Boolean>> healthCheck() {
        LOG.info(String.format("Health Check at %s", new Date()));
        return () -> new ResponseEntity<>(true, HttpStatus.OK);
    }
}
