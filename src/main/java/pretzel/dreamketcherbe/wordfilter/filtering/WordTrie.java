package pretzel.dreamketcherbe.wordfilter.filtering;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;

@Getter
public class WordTrie {

    private final WordNode root = new WordNode();

    public void insert(List<String> words) {
        for (String word : words) {
            WordNode node = root;

            for (char c : word.toCharArray()) {
                node = node.children.computeIfAbsent(c, k -> new WordNode());
            }
            node.word = word;
        }
        failLinks();
    }

    private void failLinks() {
        Queue<WordNode> queue = new LinkedList<>();
        root.fail = root;
        queue.add(root);

        while (!queue.isEmpty()) {
            WordNode current = queue.poll();

            for (var entry : current.children.entrySet()) {
                char c = entry.getKey();
                WordNode child = entry.getValue();

                WordNode failNode = current.fail;

                while (failNode != root && failNode.children.containsKey(c)) {
                    failNode = failNode.fail;
                }

                if (failNode.children.containsKey(c) && failNode.children.get(c) != child) {
                    child.fail = failNode.children.get(c);
                } else {
                    child.fail = root;
                }
                queue.add(child);
            }
        }
    }

    public boolean search(String input, int start) {
        WordNode node = root;

        for (int i = start; i < input.length(); i++) {
            char c = input.charAt(i);

            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            }

            if (node.word != null) {
                return true;
            }
        }
        return false;
    }

    public String replaceWords(String input, char replaceChar) {
        WordNode node = root;
        StringBuilder sb = new StringBuilder(input);

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            }

            if (node.word != null) {
                int start = i - node.word.length() + 1;
                for (int j = start; j <= i; j++) {
                    sb.setCharAt(j, replaceChar);
                }
            }
        }
        return sb.toString();
    }

}
