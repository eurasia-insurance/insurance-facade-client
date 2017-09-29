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
public class CallbackRequestFacade implements RequestAcceptor<CallbackRequest> {

    @Override
    public CallbackRequest acceptAndReply(CallbackRequest request) {
	Requests.preSave(request);
	CallbackRequest saved = persistRequest(request);
	setupNotifications(saved);
	logInsuranceRequestAccepted(saved);
	return saved;
    }

    @Inject
    private Notifier notifier;

    private CallbackRequest setupNotifications(CallbackRequest request) {
	notifier.assignRequestNotification(NotificationChannel.PUSH, NotificationRecipientType.COMPANY,
		NotificationRequestStage.NEW_REQUEST, request);
	return request;
    }

    @Inject
    private CallbackRequestDAO dao;

    private CallbackRequest persistRequest(final CallbackRequest request) {
	return dao.save(request);
    }

    @Inject
    private Logger logger;

    private CallbackRequest logInsuranceRequestAccepted(CallbackRequest request) {
	logger.info(String.format("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	));
	return request;
    }
}
