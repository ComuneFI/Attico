package it.linksmt.assatti.datalayer.domain.util;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        String str = jp.getText().trim();
        if(str != null && !str.isEmpty()) {
        	str = str.replaceAll(",", ".");
        	return new BigDecimal(str);
        }else {
        	return null;
        }
    }
}
