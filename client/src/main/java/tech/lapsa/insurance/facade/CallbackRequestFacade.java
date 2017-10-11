package tech.lapsa.insurance.facade;

import javax.ejb.Local;

import com.lapsa.insurance.domain.CallbackRequest;

@Local
public interface CallbackRequestFacade extends Acceptor<CallbackRequest> {

    <T extends CallbackRequest> T acceptAndReply(T request);

}