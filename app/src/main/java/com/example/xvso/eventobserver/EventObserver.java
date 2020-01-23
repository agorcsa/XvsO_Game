package com.example.xvso.eventobserver;

import androidx.lifecycle.Observer;

public class EventObserver<T> implements Observer<Event<T>> {

    private OnEventChange onEventChange;

    // constructor
    public EventObserver(OnEventChange onEventChange) {
        this.onEventChange = onEventChange;
    }

    @Override
    public void onChanged(Event<T> tEvent) {

        if (tEvent != null && tEvent.getContentIfNotHandled() != null && onEventChange != null) {
            onEventChange.onUnhandledContent(tEvent.getContentIfNotHandled());
        }
    }

    interface OnEventChange<T> {
        void onUnhandledContent(T data);
    }
}


