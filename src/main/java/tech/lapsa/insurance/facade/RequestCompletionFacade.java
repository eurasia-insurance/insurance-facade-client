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

    Request transactionUncomplete(Request request,
	    User user,
	    String note,
	    TransactionProblem transactionProblem, boolean paidable)
	    throws IllegalState, IllegalArgument;

    Request transactionCompleteWithPayment(Request request,
	    User user,
	    String note,
	    String agreementNumber,
	    String paymentMethodName,
	    Double paymentAmount,
	    Currency paymentCurrency,
	    Instant paymentInstant,
	    String paymentReference) throws IllegalState, IllegalArgument;

    Request transactionComplete(Request request,
	    User user,
	    String note,
	    String agreementNumber)
	    throws IllegalState, IllegalArgument;

}
