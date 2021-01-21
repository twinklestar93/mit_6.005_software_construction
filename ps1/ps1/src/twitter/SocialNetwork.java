/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followGraph = new HashMap<String, Set<String>>();
        tweets.forEach(tweet -> {
            Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
            // one can not be a follower to himself/herself
            for (String user : mentionedUsers) {
                if (user.equalsIgnoreCase(tweet.getAuthor())) {
                    mentionedUsers.remove(user);
                }
            }
            if (followGraph.keySet().contains(tweet.getAuthor())) {
                followGraph.get(tweet.getAuthor()).addAll(mentionedUsers);
            } else {
                followGraph.put(tweet.getAuthor(), mentionedUsers);
            }
        });
        return followGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        List<String> influencers = new ArrayList<String>();
        Map<String, Integer> followerCount = new HashMap<String, Integer>();
        for (String user : followsGraph.keySet()) {
            for (String follower : followsGraph.get(user)) {
                if (followerCount.keySet().contains(follower.toLowerCase())) {
                    followerCount.put(follower.toLowerCase(), followerCount.get(follower.toLowerCase()) + 1);
                } else {
                    followerCount.put(follower.toLowerCase(), 1);
                }
            }
        }
        // reference: https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
        followerCount = followerCount.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, HashMap::new));
        followerCount.keySet().forEach(user -> influencers.add(user));
        return influencers;
    }

}
