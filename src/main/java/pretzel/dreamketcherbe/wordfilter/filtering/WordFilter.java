package pretzel.dreamketcherbe.wordfilter.filtering;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WordFilter {

    private WordTrie badWordTrie = new WordTrie();
    private WordTrie allowedWordTrie = new WordTrie();

    public void reload(List<String> badWords, List<String> allowedWords) {
        WordTrie badTrie = new WordTrie();
        badTrie.insert(badWords);
        this.badWordTrie = badTrie;

        WordTrie allowedTrie = new WordTrie();
        allowedTrie.insert(allowedWords);
        this.allowedWordTrie = allowedTrie;
    }

    public String filter(String input) {
        List<int[]> allowedRanges = findAllowedRanges(input);

        WordNode node = badWordTrie.getRoot();
        StringBuilder sb = new StringBuilder(input);
        int length = input.length();

        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            while (node != badWordTrie.getRoot() && !node.children.containsKey(c)) {
                node = node.fail;
            }

            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            }

            WordNode temp = node;
            while (temp != badWordTrie.getRoot()) {
                if (temp.word != null) {
                    int start = i - temp.word.length() + 1;
                    int end = i;
                    if (!isInAllowedRanges(start, end, allowedRanges)) {
                        for (int j = start; j <= end; j++) {
                            sb.setCharAt(j, '@');
                        }
                    }
                }
                temp = temp.fail;
            }
        }

        return sb.toString();
    }

    private List<int[]> findAllowedRanges(String input) {
        List<int[]> ranges = new ArrayList<>();
        int length = input.length();

        for (int i = 0; i < length; i++) {
            WordNode node = allowedWordTrie.getRoot();

            for (int j = i; j < length; j++) {
                char c = input.charAt(j);

                while (node != allowedWordTrie.getRoot() && !node.children.containsKey(c)) {
                    node = node.fail;
                }

                if (node.children.containsKey(c)) {
                    node = node.children.get(c);
                }

                if (node.word != null) {
                    ranges.add(new int[]{j - node.word.length() + 1, j});
                    break;
                }
            }
        }

        return ranges;
    }

    private boolean isInAllowedRanges(int start, int end, List<int[]> ranges) {
        for (int[] range : ranges) {
            if (start >= range[0] && end <= range[1]) {
                return true;
            }
        }
        return false;
    }

}