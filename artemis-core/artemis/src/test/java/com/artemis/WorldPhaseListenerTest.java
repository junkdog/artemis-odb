package com.artemis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Arrays;

import static org.mockito.Mockito.times;

/**
 * @author Daan van Yperen
 */
public class WorldPhaseListenerTest {

    abstract class PhaseTestSystem extends BaseSystem implements ArtemisPhaseListener {
    }

    ;

    @Mock
    PhaseTestSystem phaseTestSystem;

    @Captor
    ArgumentCaptor<ArtemisPhaseListener.Phase> phaseCaptor;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void When_world_created_and_disposed_Should_call_phase_listener_phases() {
        World world = new World(new WorldConfiguration().setSystem(phaseTestSystem));
        world.process();
        world.dispose();

        Mockito.verify(phaseTestSystem, times(4)).onPhase(phaseCaptor.capture());

        Assert.assertEquals(
                Arrays.asList(ArtemisPhaseListener.Phase.PRE_INITIALIZE,
                        ArtemisPhaseListener.Phase.POST_INITIALIZE,
                        ArtemisPhaseListener.Phase.PRE_DISPOSE,
                        ArtemisPhaseListener.Phase.POST_DISPOSE)
                , phaseCaptor.getAllValues());
    }

}
