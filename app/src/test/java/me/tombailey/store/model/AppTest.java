package me.tombailey.store.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tomba on 25/02/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class AppTest {

    @Test
    public void whenGetId_shouldGetId() {
        //arrange
        String expectedId = "42";

        //act
        App underTest = new App(expectedId, null, null, 0, 0, null, 0, 0, null);

        //assert
        assertThat(underTest.getId(), is(expectedId));
    }

    @Test
    public void whenGetName_shouldGetName() {
        //arrange
        String expectedName = "Arthur Dent";

        //act
        App underTest = new App(null, expectedName, null, 0, 0, null, 0, 0, null);

        //assert
        assertThat(underTest.getName(), is(expectedName));
    }

    @Test
    public void whenGetDescription_shouldGetDescription() {
        //arrange
        String expectedDescription = "thanks for all the fish";

        //act
        App underTest = new App(null, null, expectedDescription, 0, 0, null, 0, 0, null);

        //assert
        assertThat(underTest.getDescription(), is(expectedDescription));
    }

    @Test
    public void whenGetDownloadCount_shouldGetDownloadCount() {
        //arrange
        long expectedDownloadCount = 42;

        //act
        App underTest = new App(null, null, null, expectedDownloadCount, 0, null, 0, 0, null);

        //assert
        assertThat(underTest.getDownloadCount(), is(expectedDownloadCount));
    }

    @Test
    public void whenGetCurrentVersionNumber_shouldGetCurrentVersionNumber() {
        //arrange
        long expectedCurrentVersionNumber = 42;

        //act
        App underTest = new App(null, null, null, 0, expectedCurrentVersionNumber, null, 0, 0, null);

        //assert
        assertThat(underTest.getCurrentVersionNumber(), is(expectedCurrentVersionNumber));
    }

    @Test
    public void whenGetCurrentVersionDate_shouldGetCurrentVersionDate() {
        //arrange
        String expectedCurrentVersionDate = "01-01-2000";

        //act
        App underTest = new App(null, null, null, 0, 0, expectedCurrentVersionDate, 0, 0, null);

        //assert
        assertThat(underTest.getCurrentVersionDate(), is(expectedCurrentVersionDate));
    }

    @Test
    public void whenGetScreenshotCount_shouldGetScreenshotCount() {
        //arrange
        int expectedScreenshotCount = 42;

        //act
        App underTest = new App(null, null, null, 0, 0, null, expectedScreenshotCount, 0, null);

        //assert
        assertThat(underTest.getScreenshotCount(), is(expectedScreenshotCount));
    }

    @Test
    public void whenGetScreenshotLink_shouldGetScreenshotLink() {
        //arrange
        String expectedId = "42";
        int expectedIndex = 0;
        String expectedScreenshotLink = "http://q6yl3es3js7j3gm3.onion/api/applications/" +
                expectedId + "/screenshots/" + expectedIndex;

        //act
        App underTest = new App(expectedId, null, null, 0, 0, null, 1, 0, null);

        //assert
        assertThat(underTest.getScreenshotLink(expectedIndex), is(expectedScreenshotLink));
    }

    @Test
    public void whenGetDownloadLink_shouldGetDownloadLink() {
        //arrange
        String expectedId = "42";
        long expectedCurrentVersionNumber = 42;
        String  expectedDownloadLink = "http://q6yl3es3js7j3gm3.onion/api/applications/" +
                expectedId + "/versions/" +
                expectedCurrentVersionNumber;

        //act
        App underTest = new App(expectedId, null, null, 0, expectedCurrentVersionNumber, null, 0, 0, null);

        //assert
        assertThat(underTest.getDownloadLink(), is(expectedDownloadLink));
    }

    @Test
    public void whenGetFeatureGraphicLink_shouldGetFeatureGraphicLink() {
        String expectedId = "42";
        String  expectedFeatureGraphicLink = "http://q6yl3es3js7j3gm3.onion/api/applications/" +
                expectedId + "/featureGraphic";

        //act
        App underTest = new App(expectedId, null, null, 0, 0, null, 0, 0, null);

        //assert
        assertThat(underTest.getFeatureGraphicLink(), is(expectedFeatureGraphicLink));
    }

    @Test
    public void whenGetIconLink_shouldGetIconLink() {
        //arrange
        String expectedId = "42";
        String  expectedIconLink = "http://q6yl3es3js7j3gm3.onion/api/applications/" +
                expectedId + "/icon";

        //act
        App underTest = new App(expectedId, null, null, 0, 0, null, 0, 0, null);

        //assert
        assertThat(underTest.getIconLink(), is(expectedIconLink));
    }

    @Test
    public void whenGetRating_shouldGetRating() {
        //arrange
        double expectedRating = 4.5;

        //act
        App underTest = new App(null, null, null, 0, 0, null, 0, expectedRating, null);

        //assert
        assertThat(underTest.getRating(), is(expectedRating));
    }

    @Test
    public void whenGetCategories_shouldGetCategories() {
        //arrange
        String[] expectedCategories = new String[] {
                "communication"
        };

        //act
        App underTest = new App(null, null, null, 0, 0, null, 0, 0, expectedCategories);

        //assert
        assertThat(underTest.getCategories(), is(expectedCategories));
    }


}