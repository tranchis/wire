package eu.superhub.wp4.monitor.core;

import net.sf.ictalive.runtime.event.Event;

public interface ReasonerModule extends IComponent {
    void handleObservation(Event event);
}
