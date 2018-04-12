package tech.lapsa.insurance.facade;

import java.time.Instant;
import java.util.Currency;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.crm.User;
import com.lapsa.insurance.elements.TransactionProblem;

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.exceptions.IllegalState;

public interface RequestCompletionFacade extends EJBConstants {

    public static final String BEAN_NAME = "RequestCompletionFacadeBean";

    @Local
    public interface RequestCompletionFacadeLocal extends RequestCompletionFacade {
    }

    @Remote
    public interface RequestCompletionFacadeRemote extends RequestCompletionFacade {
    }

    Request commentRequest(Request r, User user, String message) throws IllegalState, IllegalArgument;


    Request transactionUncomplete(Request request,
	    User user,
	    TransactionProblem transactionProblem,
	    boolean paidable) throws IllegalState, IllegalArgument;

    Request transactionCompleteWithPayment(Request request,
	    User user,
	    String agreementNumber,
	    String paymentMethodName,
	    Double paymentAmount,
	    Currency paymentCurrency,
	    Instant paymentInstant,
	    String paymentReference,
	    String payerName) throws IllegalState, IllegalArgument;

    Request transactionComplete(Request request,
	    User user,
	    String agreementNumber) throws IllegalState, IllegalArgument;
}
