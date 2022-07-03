package private_investigator;

import java.util.Comparator;

/**
 * Class for ordering 2 sentences.
 * Compare is done according to the words part only, (i.e. after omitting the timestamp part).
 */
public class SentenceHolderComparator implements Comparator<SentenceHolder> {

    @Override
    public int compare(SentenceHolder sentenceHolder1, SentenceHolder sentenceHolder2) {

        return sentenceHolder1.getSentenceWithNoTimestamp()
                .compareTo(sentenceHolder2.getSentenceWithNoTimestamp());
    }
}