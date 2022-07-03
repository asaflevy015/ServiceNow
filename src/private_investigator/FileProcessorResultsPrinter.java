package private_investigator;

import java.util.List;
import java.util.Map;

public class FileProcessorResultsPrinter {

    private static final String CHANGING_WORD_IS = "The changing word was: ";
    private static final String NO_SIMILAR_SENTENCES = "No similar sentences were found !!! ";

    public static void printResults(String fileName, List<Map<Integer, Map<SentenceHolder, List<SentenceHolder>>>> list){

        boolean emptyResults = true;

        StringBuilder sb = new StringBuilder(fileName).append("\n");
        for(int i=0; i<fileName.length()+5; i++){
            sb.append("-");
        }
        System.out.println(sb);

        if(!list.isEmpty()){
            for(Map<Integer, Map<SentenceHolder, List<SentenceHolder>>> similarSentences : list){
                if(!similarSentences.isEmpty()){
                    printMap(similarSentences);
                    emptyResults = false;
                }
            }
        }

        if(emptyResults){
            System.out.println(NO_SIMILAR_SENTENCES);
        }

        System.out.println();
    }

    private static void printMap(Map<Integer, Map<SentenceHolder, List<SentenceHolder>>> similarSentencesMap) {

        for(Integer changingWordIndex : similarSentencesMap.keySet()){

            Map<SentenceHolder, List<SentenceHolder>> sentenceHolderSimilarSentences = similarSentencesMap.get(changingWordIndex);

            for(SentenceHolder sentenceHolder : sentenceHolderSimilarSentences.keySet()){

                StringBuilder sb = new StringBuilder();
                sb.append(sentenceHolder.getOrigSentence()).append("\n");

                StringBuilder sbChangingWords = new StringBuilder();
                sbChangingWords.append(sentenceHolder.getWords()[changingWordIndex]).append(", ");

                List<SentenceHolder> similarSentences = sentenceHolderSimilarSentences.get(sentenceHolder);
                for(SentenceHolder similarSentenceHolder : similarSentences){
                    sb.append(similarSentenceHolder.getOrigSentence()).append("\n");
                    sbChangingWords.append(similarSentenceHolder.getWords()[changingWordIndex]).append(", ");
                }
                sb.append(CHANGING_WORD_IS).append(sbChangingWords);
                sb.setLength(sb.length()-2);
                System.out.println(sb);
            }
        }
    }
}
