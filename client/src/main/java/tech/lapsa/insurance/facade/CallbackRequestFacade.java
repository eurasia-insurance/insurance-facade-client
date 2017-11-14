package tech.lapsa.insurance.facade;

import javax.ejb.Local;

import com.lapsa.insurance.domain.CallbackRequest;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

@Local
public interface CallbackRequestFacade extends Acceptor<CallbackRequest> {

    <T extends CallbackRequest> T acceptAndReply(T request) throws IllegalArgument, IllegalState;

}