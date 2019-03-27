package org.rchtech.realex.three.ds;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.rchtech.realex.three.ds.model.EcommerceInfoExt;
import org.rchtech.realex.three.ds.response.VerifyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.global.api.ServicesContainer;
import com.global.api.entities.ThreeDSecure;
import com.global.api.entities.Transaction;
import com.global.api.entities.enums.CvnPresenceIndicator;
import com.global.api.entities.exceptions.ConfigurationException;
import com.global.api.paymentMethods.CreditCardData;
import com.global.api.serviceConfigs.GatewayConfig;

@Service
public class Realex3DSService {

	@Value("${secret}")
	private String secret;

	@Value("${sandbox}")
	private String sandbox;

	@Value("${merchant}")
	private String merchant;

	@Value("${account}")
	private String account;

	@Value("${name}")
	private String name;

	@Value("${cardnumber}")
	private String cardNumber;

	private String orderId;

	private final CreditCardData card = new CreditCardData();
	private final GatewayConfig config = new GatewayConfig();

	public VerifyResponse verifyEnrolled() throws ConfigurationException {
		setUp();
		ServicesContainer.configureService(config);
		VerifyResponse response = null;
		try {

			// send the Verify-Enrolled request to the gateway
			if (card.verifyEnrolled(new BigDecimal("129.99"), "EUR")) {
				ThreeDSecure threeDsEnrollmentDetails = card.getThreeDSecure();
				System.out.println("Order ID : " + threeDsEnrollmentDetails.getOrderId());
				this.orderId = threeDsEnrollmentDetails.getOrderId();

				// get the details necessary to redirect the customer to the ACS page
				String pareq = threeDsEnrollmentDetails.getPayerAuthenticationRequest();
				String acsUrl = threeDsEnrollmentDetails.getIssuerAcsUrl();
				response = new VerifyResponse(pareq, acsUrl, this.orderId);

				System.out.println("PaReq:\n" + pareq + "\nACS Url: \n" + acsUrl);
			}

		} catch (Exception e) {
			System.err.println(e);
		}
		return response;
	}

	public String verifyAuthenticationResult(final String paRes)
			throws ConfigurationException, UnsupportedEncodingException {

		String response = "";

		String decodedPaRes = java.net.URLDecoder.decode(paRes, StandardCharsets.UTF_8.name());

		ServicesContainer.configureService(config);
		try {
			if (card.verifySignature(decodedPaRes, new BigDecimal("129.99"), "EUR", orderId)) {
				ThreeDSecure threeDsigVerificationDetails = card.getThreeDSecure();

				// if the signature verification was successful grab the 3D Secure data
				String eci = threeDsigVerificationDetails.getEci();
				String xid = threeDsigVerificationDetails.getXid();
				String cavv = threeDsigVerificationDetails.getCavv();
				String status = threeDsigVerificationDetails.getStatus();

				System.out.println("ECI : " + eci + "\nXID : " + xid + "\nCavv : " + cavv + "\nStatus : " + status);

				response = processAuthorization(eci, xid, cavv);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private String processAuthorization(final String eci, final String xid, final String cavv)
			throws ConfigurationException {
		ServicesContainer.configureService(config);

		card.setCvn("131");
		card.setCvnPresenceIndicator(CvnPresenceIndicator.Present);

		// supply the details from the 3D Secure verify-signature response
		// Query ongoing with Realex regarding population of this object
		EcommerceInfoExt ecommerceInfo = new EcommerceInfoExt();
		ecommerceInfo.setCavv("AAACBllleHchZTBWIGV4AAAAAAA=");
		ecommerceInfo.setXid("crqAeMwkEL9r4POdxpByWJ1/wYg=");
		ecommerceInfo.setEci("5");

		try {
			Transaction response = card.charge(new BigDecimal("199.99")).withOrderId(orderId)
					.withEcommerceInfo(ecommerceInfo).withCurrency("EUR").execute();
			System.out.println(response.getResponseMessage());
			return response.getResponseMessage() + " 3DS Transaction";
		} catch (Exception e) {
			System.err.println(e);
		}

		return "";
	}

	private void setUp() {
		setConfig();
		setCard();
	}

	private void setCard() {
		card.setNumber(cardNumber);
		card.setExpMonth(12);
		card.setExpYear(2025);
		card.setCardHolderName(name);
	}

	private void setConfig() {
		config.setMerchantId(merchant);
		config.setAccountId(account);
		config.setSharedSecret(secret);
		config.setServiceUrl(sandbox);
	}

}
