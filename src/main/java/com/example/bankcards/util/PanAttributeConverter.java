package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@Component
public class PanAttributeConverter implements AttributeConverter<String, String> {
	
    private static CardEncryptor encryptor;
    
    @Autowired
    public void setEncryptor(CardEncryptor encryptor) {
		// TODO Auto-generated method stub
    	PanAttributeConverter.encryptor = encryptor;
	}
    
	@Override
	public String convertToDatabaseColumn(String attribute) {
		// TODO Auto-generated method stub
		return encryptor.encrypt(attribute);
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		// TODO Auto-generated method stub
		return encryptor.decrypt(dbData);
	}
 
}
