package tech.lapsa.insurance.facade;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.CallbackRequest;

public interface CallbackRequestFacade extends Acceptor<CallbackRequest> {

    @Local
    public interface CallbackRequestFacadeLocal extends CallbackRequestFacade {
    }

    @Remote
    public interface CallbackRequestFacadeRemote extends CallbackRequestFacade {
    }
}