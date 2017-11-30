package tech.lapsa.insurance.facade.drivenBeans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.time.Instant;
import java.util.Currency;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.shared.entity.XmlInvoiceHasPaidEvent;
import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.javax.jms.service.JmsReceiverServiceDrivenBean;

@MessageDriven(mappedName = EpaymentDestinations.INVOICE_HAS_PAID)
public class JmsInvoiceHasPaidDrivenBean extends JmsReceiverServiceDrivenBean<XmlInvoiceHasPaidEvent> {

    public JmsInvoiceHasPaidDrivenBean() {
	super(XmlInvoiceHasPaidEvent.class);
    }

    @Inject
    private InsuranceRequestFacade insuranceRequests;

    @Override
    public void receiving(XmlInvoiceHasPaidEvent entity, Properties properties) {
	final String methodName = entity.getMethod();
	final Integer id = Integer.valueOf(entity.getExternalId());
	final Instant paid = entity.getInstant();
	final Double amount = entity.getAmount();
	final Currency currency = entity.getCurrency();
	final String ref = entity.getReferenceNumber();
	reThrowAsUnchecked(() -> insuranceRequests.markPaymentSuccessful(id, methodName, paid, amount, currency, ref));
    }
}
