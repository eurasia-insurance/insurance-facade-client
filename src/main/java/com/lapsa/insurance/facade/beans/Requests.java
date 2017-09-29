package com.lapsa.insurance.facade.beans;

import java.time.LocalDateTime;

import com.lapsa.insurance.domain.CallbackRequest;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.ObtainingData;
import com.lapsa.insurance.domain.PaymentData;
import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.elements.ObtainingMethod;
import com.lapsa.insurance.elements.ObtainingStatus;
import com.lapsa.insurance.elements.PaymentMethod;
import com.lapsa.insurance.elements.PaymentStatus;
import com.lapsa.insurance.elements.ProgressStatus;
import com.lapsa.insurance.elements.RequestStatus;

final class Requests {

    private Requests() {
    }

    static void preSave(InsuranceRequest request) {
	preDates(request);
	preStatus(request);
	preProgressStatus(request);

	prePayment(request);
	preObtaining(request);
    }

    static void preSave(CallbackRequest request) {
	preDates(request);
	preStatus(request);
	preProgressStatus(request);
    }

    // PRIVATE

    private static void preProgressStatus(Request request) {
	if (request.getProgressStatus() == null)
	    request.setProgressStatus(ProgressStatus.NEW);
    }

    protected static void preStatus(Request request) {
	if (request.getStatus() == null)
	    request.setStatus(RequestStatus.OPEN);
    }

    private static void preObtaining(InsuranceRequest request) {
	if (request.getObtaining() == null)
	    request.setObtaining(new ObtainingData());
	if (request.getObtaining().getMethod() == null)
	    request.getObtaining().setMethod(ObtainingMethod.UNDEFINED);
	if (request.getObtaining().getStatus() == null)
	    request.getObtaining().setStatus(ObtainingStatus.UNDEFINED);
    }

    private static void prePayment(InsuranceRequest request) {
	if (request.getPayment() == null)
	    request.setPayment(new PaymentData());
	if (request.getPayment().getMethod() == null)
	    request.getPayment().setMethod(PaymentMethod.UNDEFINED);
	if (request.getPayment().getStatus() == null)
	    request.getPayment().setStatus(PaymentStatus.UNDEFINED);
    }

    private static void preDates(final Request request) {
	if (request.getCreated() == null)
	    request.setCreated(LocalDateTime.now());
	request.setUpdated(LocalDateTime.now());
    }

}
