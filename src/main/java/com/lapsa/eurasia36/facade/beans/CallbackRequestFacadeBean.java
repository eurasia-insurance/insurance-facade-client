package com.lapsa.eurasia36.facade.beans;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.eurasia36.facade.CallbackRequestFacade;
import com.lapsa.insurance.dao.CallbackRequestDAO;
import com.lapsa.insurance.domain.CallbackRequest;
import com.lapsa.insurance.mesenger.NotificationChannel;
import com.lapsa.insurance.mesenger.NotificationRecipientType;
import com.lapsa.insurance.mesenger.NotificationRequestStage;
import com.lapsa.insurance.mesenger.Notifier;

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
	notifier.assignRequestNotification(NotificationChannel.PUSH, NotificationRecipientType.COMPANY,
		NotificationRequestStage.NEW_REQUEST, request);
	return request;
    }

    @Inject
    private CallbackRequestDAO dao;

    private <T extends CallbackRequest> T persistRequest(final T request) {
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
