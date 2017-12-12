package tech.lapsa.insurance.facade.beans;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.lapsa.insurance.domain.CallbackRequest;

import tech.lapsa.insurance.dao.CallbackRequestDAO;
import tech.lapsa.insurance.dao.EJBViaCDI;
import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.insurance.facade.CallbackRequestFacade.CallbackRequestFacadeLocal;
import tech.lapsa.insurance.facade.CallbackRequestFacade.CallbackRequestFacadeRemote;
import tech.lapsa.java.commons.logging.MyLogger;

@Stateless
public class CallbackRequestFacadeBean implements CallbackRequestFacadeLocal, CallbackRequestFacadeRemote {

    // READERS

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <Y extends CallbackRequest> Y acceptAndReply(final Y request) throws IllegalArgumentException {
	return _acceptAndReply(request);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void accept(CallbackRequest request) throws IllegalArgumentException {
	_acceptAndReply(request);
    }

    // PRIVATE

    private <Y extends CallbackRequest> Y _acceptAndReply(final Y request) {
	Requests.preSave(request);
	final Y saved = persistRequest(request);
	setupNotifications(saved);
	logInsuranceRequestAccepted(saved);
	return saved;
    }

    // TODO DEBUG : Push disabled temporary. Need to debug
//    @Inject
//    private NotificationFacade notifications;

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
    @EJBViaCDI
    private CallbackRequestDAO dao;

    private <Y extends CallbackRequest> Y persistRequest(final Y request) {
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
