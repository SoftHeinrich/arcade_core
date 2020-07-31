package edu.usc.softarch.arcade.topics;

import java.util.Comparator;

/**
 * @author joshua
 */
public class TopicItemByTopicNumComparator implements Comparator<TopicItem> {
	public int compare(TopicItem ti0, TopicItem ti1) {
		Integer int0 = Integer.valueOf(ti0.topicNum);
		Integer int1 = Integer.valueOf(ti1.topicNum);
		
		return int0.compareTo(int1);
	}
}
