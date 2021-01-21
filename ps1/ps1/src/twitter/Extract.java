/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        List<Tweet> sortedTweets = new ArrayList<Tweet>(tweets);
        Collections.sort(sortedTweets, getCompByTimestamp());
        // end Instant has to be after start Instant in order for Timespan to be valid
        Timespan timespan = new Timespan(sortedTweets.get(0).getTimestamp(),
                sortedTweets.get(sortedTweets.size()-1).getTimestamp());
        return timespan;
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentionedUsers = new HashSet<String>();
        Pattern regex = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_\\.]))@([A-Za-z]+[A-Za-z0-9-_]+)");
        for (Tweet tweet : tweets) {
            String text = tweet.getText();
            Matcher matcher = regex.matcher(text);

            while (matcher.find()) {
                String userMentioned = matcher.group().substring(1).toLowerCase();
                mentionedUsers.add(userMentioned);
            }
        }
        return mentionedUsers;
    }

    /**
     * Helper method to facilitate compare and sort by tweet timestamp
     * @return comparator by timestamp
     */
    private static Comparator<Tweet> getCompByTimestamp() {
        Comparator comp = new Comparator<Tweet>() {

            @Override
            public int compare(Tweet t1, Tweet t2) {
                return t1.getTimestamp().compareTo(t2.getTimestamp());
            }
        };
        return comp;
    }

}
