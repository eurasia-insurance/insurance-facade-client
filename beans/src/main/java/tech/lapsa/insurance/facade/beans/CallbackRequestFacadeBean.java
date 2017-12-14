package tech.lapsa.insurance.facade.beans;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.lapsa.insurance.domain.CallbackRequest;

import tech.lapsa.insurance.dao.CallbackRequestDAO.CallbackRequestDAORemote;
import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.insurance.facade.CallbackRequestFacade.CallbackRequestFacadeLocal;
import tech.lapsa.insurance.facade.CallbackRequestFacade.CallbackRequestFacadeRemote;
import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.logging.MyLogger;

@Stateless
public class CallbackRequestFacadeBean implements CallbackRequestFacadeLocal, CallbackRequestFacadeRemote {

    // READERS

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <Y extends CallbackRequest> Y acceptAndReply(final Y request) throws IllegalArgument {
	try {
	    return _acceptAndReply(request);
	} catch (IllegalArgumentException e) {
	    throw IllegalArgument.from(e);
	}
    }

    // PRIVATE

    @EJB
    private CallbackRequestDAORemote dao;

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(CallbackRequestFacade.class) //
	    .build();

    private <Y extends CallbackRequest> Y _acceptAndReply(final Y callbackRequest) throws IllegalArgumentException {

	MyObjects.requireNonNull(callbackRequest, "callbackRequest");

	Requests.preSave(callbackRequest);

	final Y cb;
	try {
	    cb = dao.save(callbackRequest);
	} catch (IllegalArgument e) {
	    // it should not happens
	    throw new EJBException(e.getMessage());
	}

	setupNotifications(cb);

	logger.INFO.log("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		callbackRequest.getRequester().getName(), // 1
		callbackRequest.getRequester().getEmail(), // 2
		callbackRequest.getRequester().getPhone(), // 3
		callbackRequest.getClass().getSimpleName() // 4
	);

	return cb;
    }

    // TODO DEBUG : Push disabled temporary. Need to debug
    // @EJB
    // private NotificationFacadeLocal notifications;
    private CallbackRequest setupNotifications(final CallbackRequest request) throws IllegalArgumentException {
	// notifier.newNotificationBuilder() //
	// .withEvent(NotificationRequestStage.NEW_REQUEST) //
	// .withChannel(NotificationChannel.PUSH) //
	// .withRecipient(NotificationRecipientType.COMPANY) //
	// .forEntity(request) //
	// .build() //
	// .send();
	return request;
    }

}
