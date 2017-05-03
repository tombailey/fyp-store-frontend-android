package me.tombailey.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.functions.Action2;
import rx.functions.Func0;

import static me.tombailey.store.SearchPresenter.SEARCH;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

/**
 * Created by tomba on 03/05/2017.
 */

@RunWith(PowerMockRunner.class)
public class SearchPresenterTest {

    @Test
    public void whenCreateReview_shouldStartCreateReview() {
        //arrange
        SearchPresenter searchPresenter = Mockito.spy(new SearchPresenter());
        Mockito.doNothing().when(searchPresenter).start(SEARCH);

        //act
        searchPresenter.loadApps(null);

        //assert
        Mockito.verify(searchPresenter, times(1)).start(SEARCH);
    }

    @Test
    public void whenCreate_shouldRegisterRestartables() {
        //arrange
        SearchPresenter searchPresenter = Mockito.spy(new SearchPresenter());
        Mockito.doNothing().when(searchPresenter).restartableLatestCache(anyInt(), any(Func0.class),
                any(Action2.class), any(Action2.class));

        //act
        searchPresenter.onCreate(null);

        //assert
        Mockito.verify(searchPresenter, times(1)).restartableLatestCache(eq(SEARCH), any(Func0.class),
                any(Action2.class), any(Action2.class));
    }

}
