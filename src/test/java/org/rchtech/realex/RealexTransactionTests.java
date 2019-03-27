package org.rchtech.realex;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.global.api.ServicesContainer;
import com.global.api.entities.Transaction;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.paymentMethods.CreditCardData;
import com.global.api.serviceConfigs.GatewayConfig;

public class RealexTransactionTests {

	@Test
	public void test() throws ConfigurationException {
		// configure client & request settings
		final GatewayConfig config = new GatewayConfig();
		config.setMerchantId("rchtechtest");
		config.setAccountId("internet");
		config.setSharedSecret("secret");
		config.setRebatePassword("refund");
		config.setServiceUrl("https://api.sandbox.realexpayments.com/epage-remote.cgi");

		ServicesContainer.configureService(config);

		final CreditCardData card = new CreditCardData();
		card.setNumber("4111111111111111");
		card.setExpMonth(12);
		card.setExpYear(2025);
		card.setCvn("123");
		card.setCardHolderName("Joe Smith");

		String orderId = null;
		String paymentsReference = null;
		String authCode = null;

		// Authorize
		try {
			Transaction response = card.authorize(new BigDecimal("99.99")).withCurrency("EUR")
					.withSupplementaryData("taxInfo", "VATREF", "763637283332")
					.withSupplementaryData("indentityInfo", "Passport", "PPS736353").execute();

			assertEquals("00", response.getResponseCode()); // 00 == Success
			assertEquals("[ test system ] Authorised", response.getResponseMessage()); // [ test system ] Authorised

			orderId = response.getOrderId();
			authCode = response.getAuthorizationCode();
			paymentsReference = response.getTransactionId(); // pasref

		} catch (Exception e) {
			System.err.println(e);
		}

		// Settle
		final Transaction transaction = Transaction.fromId(paymentsReference, orderId);
		try {
			Transaction response = transaction.capture(new BigDecimal("99.99")).withCurrency("EUR")
					.withSupplementaryData("taxInfo", "VATREF", "763637283332")
					.withSupplementaryData("indentityInfo", "Passport", "PPS736353").execute();

			assertEquals("00", response.getResponseCode()); // 00 == Success
			assertEquals("Settled Successfully", response.getResponseMessage()); // Settled Successfully

		} catch (Exception e) {
			System.err.println(e);
		}

		// Refund
		transaction.setAuthorizationCode(authCode);
		try {
			Transaction response = transaction.refund(new BigDecimal("99.99")).withCurrency("EUR")
					.withSupplementaryData("taxInfo", "VATREF", "763637283332")
					.withSupplementaryData("indentityInfo", "Passport", "PPS736353").execute();

			assertEquals("00", response.getResponseCode()); // 00 == Success
			assertEquals("AUTH CODE:", response.getResponseMessage().substring(0, 10)); // Rebated Successfully

		} catch (Exception e) {
			System.err.println(e);
		}

		// Cancel
		try {
			Transaction response = transaction.voidTransaction().execute();

			assertEquals("00", response.getResponseCode()); // 00 == Success
			assertEquals("Voided Successfully", response.getResponseMessage()); // Voided Successfully
		} catch (Exception e) {
			System.err.println(e);
		}

	}
}
