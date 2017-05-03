package me.tombailey.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.functions.Action2;
import rx.functions.Func0;

import static me.tombailey.store.ProxyStatusPresenter.PROXY_UPDATES;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;

/**
 * Created by tomba on 03/05/2017.
 */

@RunWith(PowerMockRunner.class)
public class ProxyStatusPresenterTest {

    @Test
    public void whenCreate_shouldRegisterRestartables() {
        //arrange
        ProxyStatusPresenter proxyStatusPresenter = Mockito.spy(new ProxyStatusPresenter());
        Mockito.doNothing().when(proxyStatusPresenter).restartableLatestCache(anyInt(), any(Func0.class),
                any(Action2.class), any(Action2.class));
        Mockito.doNothing().when(proxyStatusPresenter).start(anyInt());

        //act
        proxyStatusPresenter.onCreate(null);

        //assert
        Mockito.verify(proxyStatusPresenter, times(1)).restartableLatestCache(
                Matchers.eq(PROXY_UPDATES), any(Func0.class), any(Action2.class), any(Action2.class));
    }

}
