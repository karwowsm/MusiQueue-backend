package pl.com.karwowsm.musiqueue.ws.dto;

public abstract class Event<T extends Enum<T>> {

    public abstract T getType();
}
