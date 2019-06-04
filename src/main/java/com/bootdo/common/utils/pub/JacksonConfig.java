package com.bootdo.common.utils.pub;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JacksonConfig {
	private final static Logger logger = LoggerFactory.getLogger(JacksonConfig.class);
	
	
	public static void globalConfig(ObjectMapper om, Boolean toString){
		JsonFactory jsonFactory = om.getFactory();
		jsonFactory.configure(Feature.WRITE_NUMBERS_AS_STRINGS, toString);
		DefaultSerializerProvider dsp = new DefaultSerializerProvider.Impl();

		dsp.setNullValueSerializer(new JsonSerializer<Object>() {

			@Override
			public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
					throws IOException, JsonProcessingException {

				jgen.writeString(StringUtils.EMPTY);
			}
		});
		SimpleModule module = new SimpleModule();
		module.addSerializer(Double.class,new CustomDoubleSerializer());
		om.registerModule(module);
		om.setSerializerProvider(dsp);
		om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
		om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
}
