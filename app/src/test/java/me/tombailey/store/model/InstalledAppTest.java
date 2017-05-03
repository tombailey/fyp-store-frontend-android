package me.tombailey.store.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tomba on 03/05/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class InstalledAppTest {

    @Test
    public void whenGetAppId_shouldGetAppId() {
        //arrange
        String expectedId = "42";

        //act
        InstalledApp underTest = new InstalledApp();
        underTest.setAppId(expectedId);

        //assert
        assertThat(underTest.getAppId(), is(expectedId));
    }

    @Test
    public void whenGetVersionNumber_shouldGetVersionNumber() {
        //arrange
        long expectedVersionNumber = 42;

        //act
        InstalledApp underTest = new InstalledApp();
        underTest.setVersionNumber(expectedVersionNumber);

        //assert
        assertThat(underTest.getVersionNumber(), is(expectedVersionNumber));
    }

}
