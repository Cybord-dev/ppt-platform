package com.business.unknow.services.config;

import com.business.unknow.services.util.FacturaDefaultValues;
import com.business.unknow.services.util.helpers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelperConfig {

  @Bean
  public CdfiHelper getCdfiHelper() {
    return new CdfiHelper();
  }

  @Bean
  public FacturaHelper getFacturaHelper() {
    return new FacturaHelper();
  }

  @Bean
  public DateHelper getDateHelper() {
    return new DateHelper();
  }

  @Bean
  public NumberHelper getNumberHelper() {
    return new NumberHelper();
  }

  @Bean
  public FileHelper getFileHelper() {
    return new FileHelper();
  }

  @Bean
  public SignHelper getSignHelper() {
    return new SignHelper();
  }

  @Bean
  public FacturaDefaultValues getFacturaDefaultValues() {
    return new FacturaDefaultValues();
  }

  @Bean
  public MailHelper getMailHelper() {
    return new MailHelper();
  }

  @Bean
  public NumberTranslatorHelper getNumberTranslatorHelper() {
    return new NumberTranslatorHelper();
  }
}
