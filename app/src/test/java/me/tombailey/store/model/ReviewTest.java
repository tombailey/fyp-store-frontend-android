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
public class ReviewTest {

    @Test
    public void whenGetId_shouldGetId() {
        //arrange
        String expectedId = "42";

        //act
        Review underTest = new Review(expectedId, 0, null, null);

        //assert
        assertThat(underTest.getId(), is(expectedId));
    }

    @Test
    public void whenGetRating_shouldGetRating() {
        //arrange
        int expectedRating = 5;

        //act
        Review underTest = new Review(null, expectedRating, null, null);

        //assert
        assertThat(underTest.getRating(), is(expectedRating));
    }

    @Test
    public void whenGetDescription_shouldGetDescription() {
        //arrange
        String expectedDescription = "so long and thanks for all the fish";

        //act
        Review underTest = new Review(null, 0, expectedDescription, null);

        //assert
        assertThat(underTest.getDescription(), is(expectedDescription));
    }

    @Test
    public void whenGetDate_shouldGetDate() {
        //arrange
        String expectedDate = "01-01-2000";

        //act
        Review underTest = new Review(null, 0, null, expectedDate);

        //assert
        assertThat(underTest.getDate(), is(expectedDate));
    }

}
