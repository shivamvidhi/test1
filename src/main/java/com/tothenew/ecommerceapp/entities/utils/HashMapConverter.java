package com.tothenew.ecommerceapp.entities.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.persistence.AttributeConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(Map<String, Object> customerInfo) {

        String customerInfoJson = null;
        try {
            customerInfoJson = objectMapper.writeValueAsString(customerInfo);
        } catch (final JsonProcessingException e) {
            System.out.println("JSON writing error" + e);
        }

        return customerInfoJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        Map<String, Object> customerInfo = null;
        TypeReference<Map<String, Set<String>>> typeRef
                = new TypeReference<Map<String, Set<String>>>() {};
        try {
            customerInfo = objectMapper.readValue(customerInfoJSON, typeRef);
        } catch (final IOException e) {
            System.out.println("JSON reading error" + e);
        }

        return customerInfo;
    }

}