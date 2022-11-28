package it.linksmt.assatti.gestatti.web.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.FestiviUtils;

/**
 * REST controller of utility date.
 */
@RestController
@RequestMapping("/api")
public class DateServerResource {

    private final DateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat dfDateOnly = new SimpleDateFormat("yyyy-MM-dd");
    
    @RequestMapping(value = "/dateServer/currentdate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getCurrent() throws GestattiCatchedException {
    	try{
    		JsonObject json = new JsonObject();
    		json.addProperty("milliseconds", new Date().getTime());
    		json.addProperty("datetime", dfDateTime.format(new Date()));
    		json.addProperty("date", dfDateOnly.format(new Date()));
    		return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/dateServer/calcolaDataFinePubblicazione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> calcolaDataFinePubblicazione(@RequestBody final String info) throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject infoJson = parser.parse(info).getAsJsonObject();
	  		String dateStr = infoJson.get("start").getAsString();
            LocalDate startDate = ISODateTimeFormat.dateTimeParser().parseLocalDate(dateStr);
	  		int days = infoJson.get("days").getAsInt();
	  		LocalDate endDate = FestiviUtils.aggiungiGiorniConUltimoNonLavorativo(startDate.toDate(), days - 1);
	  		
    		JsonObject json = new JsonObject();
    		json.addProperty("milliseconds", endDate.toDate().getTime());
    		json.addProperty("datetime", dfDateTime.format(endDate.toDate()));
    		json.addProperty("date", dfDateOnly.format(endDate.toDate()));
    		return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

}
