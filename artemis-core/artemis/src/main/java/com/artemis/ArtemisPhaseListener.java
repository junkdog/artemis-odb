package com.artemis;

/**
 * Phase listener for artemis lifecycle events.
 *
 * @author Daan van Yperen
 */
public interface ArtemisPhaseListener {

    enum Phase {
        PRE_INITIALIZE,
        POST_INITIALIZE,
        PRE_DISPOSE,
        POST_DISPOSE;
    }

    void onPhase(Phase phase);
}
