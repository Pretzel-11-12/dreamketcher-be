package pretzel.dreamketcherbe.wordfilter.filtering;

import java.util.List;

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
        WordNode node = badWordTrie.getRoot();
        StringBuilder sb = new StringBuilder(input);

        for (int i = 0; i < input.length(); i++) {
            if (allowedWordTrie.search(input, i)) {
                continue;
            }

            char c = input.charAt(i);

            while (node != badWordTrie.getRoot() && !node.children.containsKey(c)) {
                node = node.fail;
            }
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            }

            if (node.word != null) {
                int start = i - node.word.length() + 1;

                for (int j = start; j <= i; j++) {
                    sb.setCharAt(j, '@');
                }
            }
        }
        return sb.toString();
    }
}
