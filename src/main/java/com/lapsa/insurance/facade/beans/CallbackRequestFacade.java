package com.lapsa.insurance.facade.beans;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.lapsa.insurance.dao.CallbackRequestDAO;
import com.lapsa.insurance.domain.CallbackRequest;
import com.lapsa.insurance.mesenger.NotificationChannel;
import com.lapsa.insurance.mesenger.NotificationRecipientType;
import com.lapsa.insurance.mesenger.NotificationRequestStage;
import com.lapsa.insurance.mesenger.Notifier;

@ApplicationScoped
public class CallbackRequestFacade {

    public void accept(CallbackRequest request) {
	acceptAndReply(request);
    }

    public CallbackRequest acceptAndReply(CallbackRequest request) {
	Requests.preSave(request);
	CallbackRequest saved = persistRequest(request);
	setupNotifications(saved);
	logInsuranceRequestAccepted(saved);
	return saved;
    }

    @Inject
    private Notifier notifier;

    private void setupNotifications(CallbackRequest request) {
	notifier.assignRequestNotification(NotificationChannel.PUSH, NotificationRecipientType.COMPANY,
		NotificationRequestStage.NEW_REQUEST, request);
    }

    @Inject
    private CallbackRequestDAO callbackRequestDAO;

    private CallbackRequest persistRequest(final CallbackRequest request) {
	CallbackRequest saved = callbackRequestDAO.save(request);
	return saved;
    }

    @Inject
    private Logger logger;

    private void logInsuranceRequestAccepted(CallbackRequest request) {
	logger.info(String.format("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	));
    }
}
