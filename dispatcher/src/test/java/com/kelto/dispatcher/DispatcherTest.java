package com.kelto.dispatcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * @author Charles Fallourd on 15/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class DispatcherTest {


    private MappedServer mappedServer;
    private static final long WAITING_TIME = 500;
    private static final long NANO_WAITING_TIME = WAITING_TIME * 1000 * 1000;

    @Before
    public void init() {
        mappedServer = new MappedServer("",0);
        mappedServer = Mockito.spy(mappedServer);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(WAITING_TIME);
                return new Integer[] { 2, 5, 8};
            }
        }).when(mappedServer).sendRequest(Mockito.any(Object[].class));
        ServerDirectory.getInstance().add(mappedServer);
    }

    @org.junit.Test
    public void testDispatch() throws Exception {
        long startTime = System.nanoTime();
        Object res = new Dispatcher().dispatch(100);
        long elapsedTime = System.nanoTime() - startTime;
        Assert.assertTrue("dispatch method wait for the request method to return a result.", elapsedTime > NANO_WAITING_TIME);
        Integer[] results = (Integer[]) res;
        Assert.assertTrue(results.length == 3);
        Assert.assertArrayEquals(new Integer[] {2, 5, 8 }, results);



    }
}