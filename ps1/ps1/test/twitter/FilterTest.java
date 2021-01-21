/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing strategy:
     *
     * Partition for writtenBy(tweets, author) -> result
     * tweets.size: 1, > 1
     * tweets.written by author: 0, 1, > 1
     *
     * Partition for inTimeSpan(tweets, timespan) -> tweetsWithinTimespan
     * tweets.size: 1, > 1
     * number of tweets within timespan: 0, 1, > 1
     *
     * Partition for containing(tweets, words) -> tweetsContainingWords
     * tweets.size: 1, > 1
     * words.size: 1, > 1
     * number of tweets having at least one word in words: 0, 1, > 1
     * Include words in different case
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "bbitdiddle", "to be or not to be", d3);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");

        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenBySingleTweetNoResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "bbitdiddle");

        assertTrue("expected empty list", writtenBy.isEmpty());
    }

    @Test
    public void testWrittenBySingleTweetSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alyssa");
        assertEquals("expected single list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByMultipleTweetsNoResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "jim");
        assertTrue("expected no tweet list", writtenBy.isEmpty());
    }

    @Test
    public void testWrittenByMultipleTweetsMultipleResults() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "bbitdiddle");
        assertEquals("expected two tweets", 2, writtenBy.size());
        assertTrue("expected list to contain tweets", writtenBy.contains(tweet2) && writtenBy.contains(tweet3));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "Bbitdiddle");
        assertEquals("expected two tweets", 2, writtenBy.size());
        assertTrue("expected list to contain tweets", writtenBy.containsAll(Arrays.asList(tweet2, tweet3)));

    }

    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }

    @Test
    public void testInTimespanSingleTweetNoResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T09:59:59Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));

        assertTrue("expected empty list", inTimespan.isEmpty());

    }

    @Test
    public void testInTimespanSingleTweetSingleResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:01:59Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.contains(tweet1));
    }

    @Test
    public void testInTimespanMultipleTweetsNoResult() {
        Instant testStart = Instant.parse("2016-02-16T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-16T12:00:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        assertTrue("expected empty list", inTimespan.isEmpty());
    }

    @Test
    public void testInTimespanMultipleTweetsSingleResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:30:00Z");

        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweet", inTimespan.contains(tweet1));
        assertEquals("expect size of list to be", 1, inTimespan.size());
    }

    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));

        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testContainingSingleTweetSingleWordNoResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("idea"));
        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingSingleTweetSingleWordSingleResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("reasonable"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweet", containing.contains(tweet1));
    }

    @Test
    public void testContainingSingleTweetMultipleWordsNoResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("hey", "alright"));
        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingSingleTweetMultipleWordsSingleResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("much"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweet", containing.contains(tweet1));
    }

    @Test
    public void testContainingMultipleTweetsSingleWordNoResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("dream"));
        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingMultipleTweetsSingleWordSingleResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("minutes"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweet", containing.contains(tweet2));
        assertEquals("expect the list size to be", 1, containing.size());
    }

    @Test
    public void testContainingMultipleTweetsMultipleWordsNoResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("cheap", "quiet"));
        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingMultipleTweetsMultipleWordsSingleResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("not", "hell"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected the list contain tweet", containing.contains(tweet3));
        assertEquals("expected list size to be", 1, containing.size());
    }

    @Test
    public void testContainingMultipleTweetsMultipleWordsMultipleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("talk", "be"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
        assertEquals("expected same order", 2, containing.indexOf(tweet3));
    }


    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     *
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
