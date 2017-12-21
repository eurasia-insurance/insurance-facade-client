package tech.lapsa.insurance.facade.drivenBeans;

import java.time.Instant;
import java.util.Currency;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import tech.lapsa.epayment.shared.entity.XmlInvoiceHasPaidEvent;
import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.insurance.facade.InsuranceRequestFacade.InsuranceRequestFacadeRemote;
import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.exceptions.IllegalState;
import tech.lapsa.javax.jms.service.JmsReceiverServiceDrivenBean;

@MessageDriven(mappedName = EpaymentDestinations.INVOICE_HAS_PAID)
public class InvoiceHasPaidDrivenBean extends JmsReceiverServiceDrivenBean<XmlInvoiceHasPaidEvent> {

    public InvoiceHasPaidDrivenBean() {
	super(XmlInvoiceHasPaidEvent.class);
    }

    @Override
    public void receiving(final XmlInvoiceHasPaidEvent entity, final Properties properties)
	    throws IllegalArgumentException, IllegalStateException {
	_receiving(entity, properties);
    }

    // PRIVATE

    @EJB
    private InsuranceRequestFacadeRemote insuranceRequests;

    private void _receiving(final XmlInvoiceHasPaidEvent entity, final Properties properties)
	    throws IllegalArgumentException, IllegalStateException {
	final String methodName = entity.getMethod();
	final Integer id = Integer.valueOf(entity.getExternalId());
	final Instant paid = entity.getInstant();
	final Double amount = entity.getAmount();
	final Currency currency = entity.getCurrency();
	final String ref = entity.getReferenceNumber();
	try {
	    insuranceRequests.completePayment(id, methodName, paid, amount, currency, ref);
	} catch (final IllegalArgument e) {
	    throw e.getRuntime();
	} catch (final IllegalState e) {
	    throw e.getRuntime();
	}
    }
}
