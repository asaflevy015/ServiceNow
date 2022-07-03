package private_investigator;

public class SentenceHolder implements Comparable<SentenceHolder>{

    private final static int SENTENCE_START_INDEX = 20;
    private final String origSentence;
    private final String sentenceWithNoTimestamp;
    private String[] words;

    public SentenceHolder(String sentence) {
        origSentence = sentence;
        sentenceWithNoTimestamp = origSentence.substring(SENTENCE_START_INDEX);
        setWords();
    }

    public String getOrigSentence() {
        return origSentence;
    }

    public String getSentenceWithNoTimestamp() {
        return sentenceWithNoTimestamp;
    }

    public String[] getWords() {
        return words;
    }

    private void setWords() {
        words = sentenceWithNoTimestamp.split(" ");
    }

    public int compareTo(SentenceHolder other){

        return this.sentenceWithNoTimestamp.compareTo(other.getSentenceWithNoTimestamp());
    }

    /**
     * Returns whether passed sentence is similar to this sentence.
     *
     * Similar = the sentences differ from one another by only one word at the most
     *           in the same index.
     */
    public int isSimilar(SentenceHolder otherSentenceHolder) {

        String[] words = this.getWords();
        String[] otherWords = otherSentenceHolder.getWords();

        if(words.length != otherWords.length){
            return -1;
        }

        int changingWordIndex = -1;

        for(int i=0; i<words.length; i++){

            if(!words[i].equals(otherWords[i])){

                // No changing word was found yet.
                if(changingWordIndex == -1) {
                    changingWordIndex = i;
                }else{ // A second changing word has been found; sentences are not similar.
                    return -1;
                }
            }
        }

        return changingWordIndex;
    }

    @Override
    public String toString(){
        return sentenceWithNoTimestamp;
    }
}
