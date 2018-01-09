package tech.lapsa.insurance.facade.beans;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import tech.lapsa.insurance.facade.PingClient;
import tech.lapsa.insurance.facade.PingClient.PingClientLocal;
import tech.lapsa.insurance.facade.PingClient.PingClientRemote;
import tech.lapsa.java.commons.exceptions.IllegalState;

@Stateless(name = PingClient.BEAN_NAME)
public class PingClientBean implements PingClientLocal, PingClientRemote {

    @EJB
    private tech.lapsa.esbd.dao.PingService.PingServiceRemote esbdPing;

    @EJB
    private tech.lapsa.insurance.dao.PingService.PingServiceRemote insurancePersistencePing;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void fullPing() throws IllegalState {
	esbdPing.ping();
	insurancePersistencePing.ping();
    }
}
