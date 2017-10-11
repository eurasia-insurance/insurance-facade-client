package tech.lapsa.insurance.facade;

import com.lapsa.insurance.domain.CallbackRequest;

public interface CallbackRequestFacade extends Acceptor<CallbackRequest> {

    <T extends CallbackRequest> T acceptAndReply(T request);

}