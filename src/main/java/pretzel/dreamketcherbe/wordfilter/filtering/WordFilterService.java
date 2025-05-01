package pretzel.dreamketcherbe.wordfilter.filtering;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordFilterService {

    private final WordFilter wordFilter;

    public String filter(String input) {
        return wordFilter.filter(input);
    }
}
