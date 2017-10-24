package tech.lapsa.insurance.facade.beans;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.insurance.domain.CallbackRequest;

import tech.lapsa.insurance.dao.CallbackRequestDAO;
import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.insurance.notifier.NotificationChannel;
import tech.lapsa.insurance.notifier.NotificationRecipientType;
import tech.lapsa.insurance.notifier.NotificationRequestStage;
import tech.lapsa.insurance.notifier.Notifier;
import tech.lapsa.java.commons.logging.MyLogger;

@Stateless
public class CallbackRequestFacadeBean implements CallbackRequestFacade {

    @Override
    public <T extends CallbackRequest> T acceptAndReply(T request) {
	Requests.preSave(request);
	T saved = persistRequest(request);
	setupNotifications(saved);
	logInsuranceRequestAccepted(saved);
	return saved;
    }

    @Inject
    private Notifier notifier;

    private CallbackRequest setupNotifications(CallbackRequest request) {

	notifier.newNotificationBuilder() //
		.withEvent(NotificationRequestStage.NEW_REQUEST) //
		.withChannel(NotificationChannel.PUSH) //
		.withRecipient(NotificationRecipientType.COMPANY) //
		.forEntity(request) //
		.build() //
		.send();

	return request;
    }

    @Inject
    private CallbackRequestDAO dao;

    private <T extends CallbackRequest> T persistRequest(final T request) {
	return dao.save(request);
    }

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withPackageNameOf(CallbackRequestFacade.class) //
	    .build();

    private CallbackRequest logInsuranceRequestAccepted(CallbackRequest request) {
	logger.INFO.log("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	);
	return request;
    }
}
