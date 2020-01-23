package com.example.xvso.eventobserver;

public class Event<T> {

    private boolean hasBeenHandled = false;

    // type parameter of com.example.xvso.eventobserver.Event
    private T content;

    // constructor
    public Event(T content) {
        this.content = content;
    }

    // getter
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    // returns true is it has been handled
    // returns false if it has not been handled
    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

}
