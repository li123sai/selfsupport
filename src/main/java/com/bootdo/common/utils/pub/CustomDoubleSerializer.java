package com.bootdo.common.utils.pub;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * double输出不以科学计数法显示
 * 取小数点后几位
 * @author zhuxj
 *
 */
public class CustomDoubleSerializer extends JsonSerializer<Double> {

	@Override
	public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		DecimalFormat df=new DecimalFormat("0.0000");
		BigDecimal d = new BigDecimal(value.toString()).setScale(4,BigDecimal.ROUND_HALF_UP);
		gen.writeNumber(df.format(d));
	}

	@Override
	public Class<Double> handledType() {
		return Double.class;
	}

}