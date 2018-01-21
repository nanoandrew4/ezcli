package ezcli.modules.ezcli_core.modularity;

/**
 * Used in Module for triggering events, and determining what stage of the process an event is at.
 * Allows modules to intervene before or after an event takes place.
 *
 * @see Module
 */
public enum EventState {
    PRE_EVENT, POST_EVENT
}
