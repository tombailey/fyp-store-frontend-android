package me.tombailey.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.functions.Action2;
import rx.functions.Func0;

import static me.tombailey.store.AppPresenter.CREATE_REVIEW;
import static me.tombailey.store.AppPresenter.DOWNLOAD_APP;
import static me.tombailey.store.AppPresenter.LOAD_ICON;
import static me.tombailey.store.AppPresenter.LOAD_REVIEWS;
import static me.tombailey.store.AppPresenter.OPEN_APP;
import static me.tombailey.store.AppPresenter.SHOW_APP;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;

/**
 * Created by tomba on 03/05/2017.
 */

@RunWith(PowerMockRunner.class)
public class AppPresenterTest {

    @Test
    public void whenShowApp_shouldStartShowApp() {
        //arrange
        AppPresenter appPresenter = Mockito.spy(new AppPresenter());
        Mockito.doNothing().when(appPresenter).start(SHOW_APP);

        //act
        appPresenter.showApp(null);

        //assert
        Mockito.verify(appPresenter, times(1)).start(SHOW_APP);
    }

    @Test
    public void whenLoadIcon_shouldStartLoadIcon() {
        //arrange
        AppPresenter appPresenter = Mockito.spy(new AppPresenter());
        Mockito.doNothing().when(appPresenter).start(LOAD_ICON);

        //act
        appPresenter.loadIcon(null);

        //assert
        Mockito.verify(appPresenter, times(1)).start(LOAD_ICON);
    }

    @Test
    public void whenLoadReviews_shouldStartLoadReviews() {
        //arrange
        AppPresenter appPresenter = Mockito.spy(new AppPresenter());
        Mockito.doNothing().when(appPresenter).start(LOAD_REVIEWS);

        //act
        appPresenter.loadReviews(null);

        //assert
        Mockito.verify(appPresenter, times(1)).start(LOAD_REVIEWS);
    }

    @Test
    public void whenOpenApp_shouldStartOpenApp() {
        //arrange
        AppPresenter appPresenter = Mockito.spy(new AppPresenter());
        Mockito.doNothing().when(appPresenter).start(OPEN_APP);

        //act
        appPresenter.openApp(null);

        //assert
        Mockito.verify(appPresenter, times(1)).start(OPEN_APP);
    }

    @Test
    public void whenDownloadApp_shouldStartDownloadApp() {
        //arrange
        AppPresenter appPresenter = Mockito.spy(new AppPresenter());
        Mockito.doNothing().when(appPresenter).start(DOWNLOAD_APP);

        //act
        appPresenter.downloadApp(null);

        //assert
        Mockito.verify(appPresenter, times(1)).start(DOWNLOAD_APP);
    }

    @Test
    public void whenCreateReview_shouldStartCreateReview() {
        //arrange
        AppPresenter appPresenter = Mockito.spy(new AppPresenter());
        Mockito.doNothing().when(appPresenter).start(CREATE_REVIEW);

        //act
        appPresenter.createReview(null, null, 0);

        //assert
        Mockito.verify(appPresenter, times(1)).start(CREATE_REVIEW);
    }

    @Test
    public void whenCreate_shouldRegisterRestartables() {
        //arrange
        AppPresenter appPresenter = Mockito.spy(new AppPresenter());
        Mockito.doNothing().when(appPresenter).restartableLatestCache(anyInt(), any(Func0.class),
                any(Action2.class));
        Mockito.doNothing().when(appPresenter).restartableLatestCache(anyInt(), any(Func0.class),
                any(Action2.class), any(Action2.class));

        //act
        appPresenter.onCreate(null);

        //assert
        Mockito.verify(appPresenter, times(2)).restartableLatestCache(anyInt(), any(Func0.class),
                any(Action2.class), any(Action2.class));
        Mockito.verify(appPresenter, times(4)).restartableLatestCache(anyInt(), any(Func0.class),
                any(Action2.class));
    }

}
