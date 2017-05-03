package me.tombailey.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.functions.Action2;
import rx.functions.Func0;

import static me.tombailey.store.UpdatesPresenter.CHECK_FOR_UPDATE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

/**
 * Created by tomba on 03/05/2017.
 */

@RunWith(PowerMockRunner.class)
public class UpdatesPresenterTest {

    @Test
    public void whenCreateReview_shouldStartCreateReview() {
        //arrange
        UpdatesPresenter updatesPresenter = Mockito.spy(new UpdatesPresenter());
        Mockito.doNothing().when(updatesPresenter).start(CHECK_FOR_UPDATE);

        //act
        updatesPresenter.checkForUpdates();

        //assert
        Mockito.verify(updatesPresenter, times(1)).start(CHECK_FOR_UPDATE);
    }

    @Test
    public void whenCreate_shouldRegisterRestartables() {
        //arrange
        UpdatesPresenter updatesPresenter = Mockito.spy(new UpdatesPresenter());
        Mockito.doNothing().when(updatesPresenter).start(CHECK_FOR_UPDATE);
        Mockito.doNothing().when(updatesPresenter).restartableLatestCache(anyInt(), any(Func0.class),
                any(Action2.class), any(Action2.class));

        //act
        updatesPresenter.onCreate(null);

        //assert
        Mockito.verify(updatesPresenter, times(1)).restartableLatestCache(eq(CHECK_FOR_UPDATE), any(Func0.class),
                any(Action2.class), any(Action2.class));
    }

}
