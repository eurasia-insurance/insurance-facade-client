package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.lapsa.insurance.domain.CallbackRequest;

import tech.lapsa.insurance.dao.CallbackRequestDAO;
import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.java.commons.logging.MyLogger;

@Stateless
public class CallbackRequestFacadeBean implements CallbackRequestFacade {

    // READERS

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T extends CallbackRequest> T acceptAndReply(final T request) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _acceptAndReply(request));
    }

    // PRIVATE

    private <T extends CallbackRequest> T _acceptAndReply(final T request) {
	Requests.preSave(request);
	final T saved = persistRequest(request);
	setupNotifications(saved);
	logInsuranceRequestAccepted(saved);
	return saved;
    }

    // TODO DEBUG : Push disabled temporary. Need to debug
    // @Inject
    // private Notifier notifier;

    private CallbackRequest setupNotifications(final CallbackRequest request) {
	// notifier.newNotificationBuilder() //
	// .withEvent(NotificationRequestStage.NEW_REQUEST) //
	// .withChannel(NotificationChannel.PUSH) //
	// .withRecipient(NotificationRecipientType.COMPANY) //
	// .forEntity(request) //
	// .build() //
	// .send();
	return request;
    }

    @Inject
    private CallbackRequestDAO dao;

    private <T extends CallbackRequest> T persistRequest(final T request) {
	return dao.save(request);
    }

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(CallbackRequestFacade.class) //
	    .build();

    private CallbackRequest logInsuranceRequestAccepted(final CallbackRequest request) {
	logger.INFO.log("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	);
	return request;
    }
}
