package tech.lapsa.insurance.facade;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface Acceptor<X> {

    <Y extends X> Y acceptAndReply(Y request) throws IllegalArgument;
}
