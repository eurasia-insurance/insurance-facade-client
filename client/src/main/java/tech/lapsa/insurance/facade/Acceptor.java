package tech.lapsa.insurance.facade;

public interface Acceptor<X> {

    void accept(X request) throws IllegalArgumentException;

    X acceptAndReply(X request) throws IllegalArgumentException;
}
