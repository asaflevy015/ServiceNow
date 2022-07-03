package private_investigator;

import java.io.File;
import java.util.*;

public class FileProcessor {

    public void process(File f) {

        // STEP 1 -
        // Uploads the file.
        List<String> fileSentences = FileUpload.upload(f);

        // STEP 2 -
        // Gets a TreeMap in which:
        //  Key - Integer which stands for number of words in each one of the sentences in the map value.
        //  Value - List of SentenceHolder objects.
        //
        // The idea is that there is no point of comparing sentences with a DIFFERENT number of words in them
        // as they can't be similar, (according to the assignment's assumptions).
        //
        // In addition, for future scaling, this allows us parallel execution within ahead step 3; i.e. each entry
        // in the map can be executed by a separate thread as each entry contributes its own results to
        // the final output with no dependency in the input/output of/from rest of the map entries.
        Map<Integer, List<SentenceHolder>> sentences = getSentencesMap(fileSentences);

        // STEP 3 -
        // Analyzes sentences and returns the similar sentences results.
        List<Map<Integer, Map<SentenceHolder, List<SentenceHolder>>>> results = analyzeSentences(sentences);

        // STEP 4 -
        // Prints the results.
        FileProcessorResultsPrinter.printResults(f.getName(), results);
    }

    /**
     * Returns a TreeMap in which:
     *  Key - Integer which stands for number of words in each one of the sentences in the map value.
     *  Value - List of SentenceHolder objects.
     */
    private Map<Integer, List<SentenceHolder>> getSentencesMap(List<String> list) {

        Map<Integer, List<SentenceHolder>> allSentences = new TreeMap<>();

        for(String sentence : list){

            SentenceHolder sentenceHolder = new SentenceHolder(sentence);
            int numberOfWords = sentenceHolder.getWords().length;

            List<SentenceHolder> sentences = allSentences.get(numberOfWords);

            if(sentences == null){
                sentences = new ArrayList<>();
                allSentences.put(numberOfWords, sentences);
            }

            sentences.add(sentenceHolder);
        }

        return allSentences;
    }

    /**
     * Analyzes the map of sentences and returns the similar sentences results.
     */
    private List<Map<Integer, Map<SentenceHolder, List<SentenceHolder>>>>
                            analyzeSentences(Map<Integer, List<SentenceHolder>> sentences){

        List<Map<Integer, Map<SentenceHolder, List<SentenceHolder>>>> results = new ArrayList<>();

        for(List<SentenceHolder> sentenceHolderList : sentences.values()){

            // Helper map in which:
            //  Key - a compared SentenceHolder
            //  Value - map in which:
            //           Key - Changing word index
            //           Value - List of similar SentenceHolder objects
            Map<String, Map<Integer, List<SentenceHolder>>> comparedSentencesMap = new HashMap<>();

            // Orders the list.
            Collections.sort(sentenceHolderList, new SentenceHolderComparator());

            int size = sentenceHolderList.size();

            // No need to compare the last sentence as it will be compared to all sentences
            // which come before.
            for(int i=0; i<size-1; i++){

                SentenceHolder sentenceHolder = sentenceHolderList.get(i);

                // Map in which:
                //  Key - Changing word index
                //  Value - Map in which:
                //           Key - SentenceHolder (the one that is currently being compared to all sentences which follow it)
                //           Value - List of similar SentenceHolder objects
                Map<Integer, Map<SentenceHolder, List<SentenceHolder>>> similarSentences = new HashMap<>();

                for(int j=i+1; j<size; j++){

                    SentenceHolder comparedSentenceHolder = sentenceHolderList.get(j);

                    // Returns -1 if sentences are NOT similar.
                    int changingWordIndex = sentenceHolder.isSimilar(comparedSentenceHolder);

                    // Sentences are similar.
                    if(changingWordIndex >= 0){

                        Map<Integer, List<SentenceHolder>> alreadyFoundComparedSentences =
                                comparedSentencesMap.get(comparedSentenceHolder.getOrigSentence());

                        if(alreadyFoundAsCompared(comparedSentencesMap, changingWordIndex, comparedSentenceHolder)){
                            continue;
                        }

                        // Gets from the final map the map of similar sentences for the current
                        // SentenceHolder with same changing word index.
                        Map<SentenceHolder, List<SentenceHolder>> sentenceHolderSimilarSentences =
                                similarSentences.get(changingWordIndex);

                        // Map wasn't found; initializes it for the first time.
                        if(sentenceHolderSimilarSentences == null){
                            sentenceHolderSimilarSentences = new HashMap<>();
                            sentenceHolderSimilarSentences.put(sentenceHolder, new ArrayList<>());
                            similarSentences.put(changingWordIndex, sentenceHolderSimilarSentences);
                        }

                        // Updates list of similar sentences with the current COMPARED sentence (a SentenceHolder
                        // to be more accurate), which was found as similar.
                        List<SentenceHolder> similarSentencesList = sentenceHolderSimilarSentences.get(sentenceHolder);
                        similarSentencesList.add(comparedSentenceHolder);

                        // Updates the compared sentences' holder map.
                        if(alreadyFoundComparedSentences == null) {
                            alreadyFoundComparedSentences = new HashMap<>();
                            updateComparedSentencesMap(comparedSentencesMap, alreadyFoundComparedSentences,
                                                       changingWordIndex, sentenceHolder,
                                                       comparedSentenceHolder);
                        }
                    }
                }

                results.add(similarSentences);
            }
        }

        return results;
    }

    /**
     * Returns whether current sentence has already been found as similar to a previous sentence
     */
    private boolean alreadyFoundAsCompared(Map<String, Map<Integer, List<SentenceHolder>>> comparedSentencesMap,
                                           int changingWordIndex,
                                           SentenceHolder comparedSentenceHolder){

        boolean foundAsCompared = false;

        Map<Integer, List<SentenceHolder>> alreadyFoundComparedSentences =
                comparedSentencesMap.get(comparedSentenceHolder.getOrigSentence());

        // The current sentence has already been found as similar to a previous sentence;
        // since we're in the same changing index, then it means that the current COMPARED
        // sentence should be added to these 2 sentences; i.e. we now have 3 similar sentences
        // that differ in the same changing index.
        if(alreadyFoundComparedSentences != null){
            if(alreadyFoundComparedSentences.get(changingWordIndex) != null){
                alreadyFoundComparedSentences.get(changingWordIndex).add(comparedSentenceHolder);
                foundAsCompared = true;
            }
        }

        return foundAsCompared;
    }

    /**
     * Updates compared sentences map in which:
     *   Key - a compared SentenceHolder
     *   Value - map in which:
     *            Key - Changing word index
     *            Value - List of similar SentenceHolder objects
     */
    private void updateComparedSentencesMap(Map<String, Map<Integer, List<SentenceHolder>>> comparedSentencesMap,
                                            Map<Integer, List<SentenceHolder>> alreadyFoundComparedSentences,
                                            int changingWordIndex,
                                            SentenceHolder sentenceHolder,
                                            SentenceHolder comparedSentenceHolder) {

        List<SentenceHolder> list1 = new ArrayList<>();
        list1.add(sentenceHolder);
        alreadyFoundComparedSentences.put(changingWordIndex, list1);
        comparedSentencesMap.put(comparedSentenceHolder.getOrigSentence(), alreadyFoundComparedSentences);
    }
}