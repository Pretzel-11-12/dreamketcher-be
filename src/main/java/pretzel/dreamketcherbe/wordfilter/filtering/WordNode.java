package pretzel.dreamketcherbe.wordfilter.filtering;

import java.util.HashMap;
import java.util.Map;

public class WordNode {

    Map<Character, WordNode> children = new HashMap<>();
    WordNode fail;
    String word;
}
