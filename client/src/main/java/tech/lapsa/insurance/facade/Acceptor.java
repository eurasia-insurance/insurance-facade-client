package tech.lapsa.insurance.facade;

public interface Acceptor<X> {

    void accept(X request) throws IllegalArgumentException;

    <Y extends X> Y acceptAndReply(Y request) throws IllegalArgumentException;
}
