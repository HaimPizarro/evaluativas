package com.example.evaluativas.user.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class BooleanYNConverter implements AttributeConverter<Boolean, String> {
  @Override
  public String convertToDatabaseColumn(Boolean value) {
    return Boolean.TRUE.equals(value) ? "Y" : "N";
  }
  @Override
  public Boolean convertToEntityAttribute(String dbValue) {
    return "Y".equalsIgnoreCase(dbValue);
  }
}
