package tech.lapsa.insurance.facade;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

public interface Acceptor<T> {

    default <X extends T> void accept(X request) throws IllegalArgument, IllegalState {
	acceptAndReply(request);
    }

    <X extends T> X acceptAndReply(X request) throws IllegalArgument, IllegalState;
}
