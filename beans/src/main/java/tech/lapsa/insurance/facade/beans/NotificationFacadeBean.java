package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.insurance.shared.jms.InsuranceDestinations.*;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Destination;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.casco.CascoRequest;
import com.lapsa.insurance.domain.policy.PolicyRequest;

import tech.lapsa.insurance.facade.NotificationFacade.NotificationFacadeLocal;
import tech.lapsa.insurance.facade.NotificationFacade.NotificationFacadeRemote;
import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.javax.jms.client.JmsClientFactory;
import tech.lapsa.javax.jms.client.JmsEventNotificatorClient;

@Stateless
public class NotificationFacadeBean implements NotificationFacadeLocal, NotificationFacadeRemote {

    // READERS

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void send(final Notification notification) throws IllegalArgument {
	try {
	    _send(notification);
	} catch (IllegalArgumentException e) {
	    throw IllegalArgument.from(e);
	}
    }

    // PRIVATE

    @Inject
    private JmsClientFactory jmsFactory;

    // TODO REFACT : Required to send Notification object to the single JMS
    // destination and process the Notification at the recipient side. Also it
    // could removes the most of notification driven beans
    private void _send(final Notification notification) throws IllegalArgumentException {
	MyObjects.requireNonNull(notification, "notification");
	final Destination destination = resolveDestination(notification);
	final JmsEventNotificatorClient<Request> notificator = jmsFactory.createEventNotificator(destination);
	notificator.eventNotify(notification.getEntity(), notification.getProperties());
    }

    @Resource(name = NOTIFIER_NEW_POLICY_COMPANY_EMAIL)
    private Destination newPolicyCompanyEmail;

    @Resource(name = NOTIFIER_NEW_INSURANCE_COMPANY_PUSH)
    private Destination newInsuranceCompanyPush;

    @Resource(name = NOTIFIER_NEW_CALLBACK_COMPANY_PUSH)
    private Destination newCallbackCompanyPush;

    @Resource(name = NOTIFIER_NEW_POLICY_USER_EMAIL)
    private Destination newPolicyUserEmail;

    @Resource(name = NOTIFIER_NEW_CASCO_COMPANY_EMAIL)
    private Destination newCascoCompanyEmail;

    @Resource(name = NOTIFIER_NEW_CASCO_USER_EMAIL)
    private Destination newCascoUserEmail;

    @Resource(name = NOTIFIER_REQUEST_PAID_COMPANY_EMAIL)
    private Destination requestPaidCompanyEmail;

    private Destination resolveDestination(final Notification notification) throws IllegalArgumentException {
	MyObjects.requireNonNull(notification, "notification");
	final Request request = notification.getEntity();
	switch (notification.getEvent()) {
	case NEW_REQUEST:
	    switch (notification.getChannel()) {
	    case EMAIL:
		switch (notification.getRecipientType()) {
		case COMPANY:
		    if (request instanceof PolicyRequest)
			return newPolicyCompanyEmail;
		    if (request instanceof CascoRequest)
			return newCascoCompanyEmail;
		    break;
		case REQUESTER:
		    if (request instanceof PolicyRequest)
			return newPolicyUserEmail;
		    if (request instanceof CascoRequest)
			return newCascoUserEmail;
		    break;
		default:
		}
	    case PUSH:
		// TODO DEBUG : Push disabled temporary. Need to debug
		// switch (recipientType) {
		// case COMPANY:
		// if (request instanceof InsuranceRequest)
		// return newInsuranceCompanyPush;
		// if (request instanceof CallbackRequest)
		// return newCallbackCompanyPush;
		// break;
		// default:
		// }
		break;
	    default:
	    }
	    break;
	case REQUEST_PAID:
	    switch (notification.getChannel()) {
	    case EMAIL:
		switch (notification.getRecipientType()) {
		case COMPANY:
		    return requestPaidCompanyEmail;
		default:
		    break;
		}
	    default:
		break;
	    }
	    break;
	default:
	    break;
	}
	throw MyExceptions.illegalArgumentFormat(
		"Can't resolve Destination for channel '%2$s' recipient '%3$s' stage '%1$s'", // s
		notification.getEvent(), // 1
		notification.getChannel(), // 2
		notification.getRecipientType() // 3
	);
    }
}
