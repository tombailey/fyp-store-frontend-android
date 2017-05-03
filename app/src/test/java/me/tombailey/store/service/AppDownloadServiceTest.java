package me.tombailey.store.service;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import me.tombailey.store.model.App;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by tomba on 03/05/2017.
 */

@RunWith(PowerMockRunner.class)
public class AppDownloadServiceTest {

    @Test
    public void whenOnStartCommand_shouldHandleIntent() {
        //arrange
        Intent expectedIntent = mock(Intent.class);
        String expectedAction = "action";

        AppDownloadService appDownloadService = Mockito.spy(new AppDownloadService());
        Mockito.doNothing().when(appDownloadService).handleAction(anyString(), any(Intent.class));

        //act
        when(expectedIntent.getAction()).thenReturn(expectedAction);
        appDownloadService.onStartCommand(expectedIntent, 0, 0);

        //assert
        Mockito.verify(appDownloadService, times(1)).handleAction(eq(expectedAction), eq(expectedIntent));
    }

    @Test
    public void whenHandleActionAndActionIsDownload_shouldHandleDownload() {
        //arrange
        Intent intent = mock(Intent.class);
        App expectedApp = null;
        when(intent.getParcelableExtra(AppDownloadService.APP)).thenReturn(expectedApp);

        AppDownloadService appDownloadService = Mockito.spy(new AppDownloadService());
        Mockito.doNothing().when(appDownloadService).downloadApp(any(App.class));

        //act
        appDownloadService.handleAction(AppDownloadService.DOWNLOAD_APP, intent);

        //assert
        Mockito.verify(appDownloadService, times(1)).downloadApp(eq(expectedApp));
    }

    @Test
    public void whenHandleActionAndActionIsInstall_shouldHandleInstall() {
        //arrange
        Intent intent = mock(Intent.class);
        App expectedApp = null;
        when(intent.getParcelableExtra(AppDownloadService.APP)).thenReturn(expectedApp);
        File expectedFile = new File("/");
        when(intent.getStringExtra(AppDownloadService.APK_SAVE_FILE)).thenReturn("/");

        AppDownloadService appDownloadService = Mockito.spy(new AppDownloadService());
        Mockito.doNothing().when(appDownloadService).installApp(any(App.class), any(File.class));

        //act
        appDownloadService.handleAction(AppDownloadService.APP_INSTALL, intent);

        //assert
        Mockito.verify(appDownloadService, times(1)).installApp(eq(expectedApp), eq(expectedFile));
    }

    @Test
    public void whenHandleActionAndActionIsCancel_shouldHandleCancel() {
        //arrange
        Intent intent = mock(Intent.class);
        File expectedFile = new File("/");
        when(intent.getStringExtra(AppDownloadService.APK_SAVE_FILE)).thenReturn("/");

        AppDownloadService appDownloadService = Mockito.spy(new AppDownloadService());
        Mockito.doNothing().when(appDownloadService).cancelAppInstall(any(File.class));

        //act
        appDownloadService.handleAction(AppDownloadService.CANCEL_APP_INSTALL, intent);

        //assert
        Mockito.verify(appDownloadService, times(1)).cancelAppInstall(eq(expectedFile));
    }

}
