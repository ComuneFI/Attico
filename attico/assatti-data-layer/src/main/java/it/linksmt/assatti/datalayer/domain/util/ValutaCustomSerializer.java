package it.linksmt.assatti.datalayer.domain.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom Jackson serializer for displaying BigDecimal objects.
 */
public class ValutaCustomSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
    	if(value!=null){
	    	DecimalFormat df = new DecimalFormat();
	    	df.setMinimumFractionDigits(2);
	    	df.setGroupingUsed(false);
	        generator.writeString(df.format(value).replaceAll(",", "."));
    	}else{
    		generator.writeString("");
    	}
    }

}
