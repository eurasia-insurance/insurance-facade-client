package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.time.Instant;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.fin.FinCurrency;
import com.lapsa.insurance.domain.CalculationData;
import com.lapsa.insurance.domain.InsuranceProduct;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.RequesterData;
import com.lapsa.insurance.elements.PaymentMethod;
import com.lapsa.insurance.elements.PaymentStatus;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.domain.Invoice.InvoiceBuilder;
import tech.lapsa.epayment.facade.EpaymentFacade;
import tech.lapsa.insurance.dao.InsuranceRequestDAO;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.insurance.notifier.NotificationChannel;
import tech.lapsa.insurance.notifier.NotificationRecipientType;
import tech.lapsa.insurance.notifier.NotificationRequestStage;
import tech.lapsa.insurance.notifier.Notifier;
import tech.lapsa.insurance.notifier.Notifier.NotificationBuilder;
import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.java.commons.function.MyNumbers;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.java.commons.logging.MyLogger;

@Stateless
public class InsuranceRequestFacadeBean implements InsuranceRequestFacade {

    @Override
    public <T extends InsuranceRequest> T acceptAndReply(final T request) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    Requests.preSave(request);
	    final T saved = persistRequest(request);
	    setupPaymentOrder(saved);
	    setupNotifications(saved);
	    logInsuranceRequestAccepted(saved);
	    return saved;
	});
    }

    @Override
    public void markPaymentSuccessful(final Integer id, final String methodName, final Instant paymentInstant,
	    final Double amount,
	    final String paymentReference) throws IllegalArgument, IllegalState {
	reThrowAsChecked(() -> {
	    InsuranceRequest request = dao.optionalById(id)
		    .orElseThrow(() -> new IllegalArgumentException("Request not found with id " + id));
	    request.getPayment().setStatus(PaymentStatus.DONE);
	    request.getPayment().setPostReference(paymentReference);
	    request.getPayment().setPostInstant(paymentInstant);
	    request = dao.save(request);

	    request.unlazy();

	    notifier.newNotificationBuilder() //
		    .withEvent(NotificationRequestStage.REQUEST_PAID) //
		    .withChannel(NotificationChannel.EMAIL) //
		    .forEntity(request) //
		    .withRecipient(NotificationRecipientType.COMPANY) //
		    .build()
		    .send();
	});
    }

    // PRIVATE

    @Inject
    private EpaymentFacade epayments;

    private <T extends InsuranceRequest> T setupPaymentOrder(final T request) {
	if (request.getPayment() != null //
		&& MyStrings.empty(request.getPayment().getExternalId()) //
		&& PaymentMethod.PAYCARD_ONLINE.equals(request.getPayment().getMethod())) {

	    final InvoiceBuilder builder = Invoice.builder() //
		    .withGeneratedNumber() //
		    .withExternalId(request.getId());

	    final Optional<RequesterData> ord = MyOptionals.of(request.getRequester());

	    final LocalizationLanguage consumerLanguage = ord.map(RequesterData::getPreferLanguage) //
		    .orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine the language"));
	    builder.withConsumerPreferLanguage(consumerLanguage);

	    builder.withConsumerEmail(ord.map(RequesterData::getEmail) //
		    .orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine a consumer email")));

	    builder.withConsumerName(ord.map(RequesterData::getName) //
		    .orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine a consumer name")));

	    ord.map(RequesterData::getIdNumber) //
		    .ifPresent(builder::withConsumerTaxpayerNumber);

	    final Optional<CalculationData> ocd = MyOptionals.of(request.getProduct()) //
		    .map(InsuranceProduct::getCalculation);

	    builder.withCurrency(ocd.map(CalculationData::getPremiumCurrency) //
		    .map(FinCurrency::getCurrency)
		    .orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine an premium currency")));

	    builder.withItem(MyOptionals.of(request.getProductType()) //
		    .map(x -> x.regular(consumerLanguage.getLocale())) //
		    .orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine an item name")), //
		    1, //
		    ocd.map(CalculationData::getPremiumCost) //
			    .filter(MyNumbers::nonZero) //
			    .orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine an premium amount")));

	    final String invoiceNumber = reThrowAsUnchecked(() -> epayments.completeAndAccept(builder) //
		    .getNumber());

	    request.getPayment() //
		    .setExternalId(invoiceNumber);
	}
	return request;
    }

    @Inject
    private Notifier notifier;

    private <T extends InsuranceRequest> T setupNotifications(final T request) {
	final NotificationBuilder builder = notifier.newNotificationBuilder() //
		.withEvent(NotificationRequestStage.NEW_REQUEST) //
		.forEntity(request);

	switch (request.getType()) {
	case ONLINE:
	case EXPRESS:
	    builder.withChannel(NotificationChannel.EMAIL) //
		    .withRecipient(NotificationRecipientType.COMPANY) //
		    .build() //
		    .send();
	    if (request.getRequester().getEmail() != null)
		builder.withChannel(NotificationChannel.EMAIL) //
			.withRecipient(NotificationRecipientType.REQUESTER) //
			.build() //
			.send();
	case UNCOMPLETE:
	    // TODO DEBUG : Push disabled temporary. Need to debug
	    // builder.withChannel(NotificationChannel.PUSH) //
	    // .withRecipient(NotificationRecipientType.COMPANY) //
	    // .build() //
	    // .send();
	}
	return request;
    }

    @Inject
    private InsuranceRequestDAO dao;

    private <T extends InsuranceRequest> T persistRequest(final T request) {
	return dao.save(request);
    }

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(InsuranceRequestFacade.class) //
	    .build();

    private <T extends InsuranceRequest> T logInsuranceRequestAccepted(final T request) {
	logger.INFO.log("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	);
	return request;
    }

}
