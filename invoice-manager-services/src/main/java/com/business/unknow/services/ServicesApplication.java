package com.business.unknow.services;

import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.business.unknow.commons.factura.CdfiHelper;
import com.business.unknow.commons.factura.SignHelper;
import com.business.unknow.commons.util.DateHelper;
import com.business.unknow.commons.util.FacturaHelper;
import com.business.unknow.commons.util.FileHelper;
import com.business.unknow.commons.util.MailHelper;
import com.business.unknow.commons.util.NumberHelper;
import com.business.unknow.commons.util.NumberTranslatorHelper;
import com.business.unknow.commons.util.StringHelper;
import com.business.unknow.rules.suites.TimbradoSuite;
import com.business.unknow.rules.suites.facturas.CancelacionSuite;
import com.business.unknow.rules.suites.facturas.FacturaSuite;
import com.business.unknow.rules.suites.facturas.FacturaValidationSuite;
import com.business.unknow.rules.suites.payments.DeletePagoSuite;
import com.business.unknow.rules.suites.payments.PaymentCreationSuite;
import com.business.unknow.rules.suites.payments.PaymentUpdateSuite;
import com.business.unknow.services.util.FacturaDefaultValues;

/**
 * @author eej000f
 *
 */
@SpringBootApplication
public class ServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicesApplication.class, args);
	}

	@Bean
	public DeletePagoSuite getDeletePagoSuite() {
		return new DeletePagoSuite();
	}

	@Bean
	public FacturaSuite getFacturaSuite() {
		return new FacturaSuite();
	}

	@Bean
	public TimbradoSuite getFacturarSuite() {
		return new TimbradoSuite();
	}

	@Bean
	public PaymentUpdateSuite getPagoPueSuite() {
		return new PaymentUpdateSuite();
	}

	@Bean
	public PaymentCreationSuite getPagoPpdSuite() {
		return new PaymentCreationSuite();
	}

	@Bean
	public FacturaValidationSuite getFacturaValidationSuite() {
		return new FacturaValidationSuite();
	}

	@Bean
	public CancelacionSuite getCancelacionSuite() {
		return new CancelacionSuite();
	}

	@Bean
	public RulesEngine getRulesEngine() {
		return new DefaultRulesEngine();
	}

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
	public StringHelper getStringHelper() {
		return new StringHelper();
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
