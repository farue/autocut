package de.farue.autocut.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.farue.autocut.utils.StringCandidateMatcher.MatchResult;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StringCandidateMatcherTest {

    @Test
    void testEmptySentence() {
        Set<String> candidates = Set.of("hello world");
        MatchResult result = StringCandidateMatcher.findBestMatch(candidates, "");
        assertThat(result.distance).isEqualTo(11);
    }

    @Test
    void testSingleCandidate() {
        Set<String> candidates = Set.of("the world");
        MatchResult result = StringCandidateMatcher.findBestMatch(candidates, "the quick brown fox jumps over the lazy dog");
        System.out.println(result);
        assertThat(result.tokens).containsExactly("the", null, "brown", null, null, null, null, null, null);
        assertThat(result.candidate).isEqualTo("the world");
        assertThat(result.distance).isEqualTo(4);
    }

    @Test
    void testCandidateWithLowerErrorRatioWins() {
        Set<String> candidates = Set.of(
            "hello world",
            "bronfos", // distance = 3, errorRatio = 0.33
            "the fast brown dog" // distance = 5, errorRatio = 0.26
        );
        MatchResult result = StringCandidateMatcher.findBestMatch(candidates, "the quick brown fox jumps over the lazy dog");
        System.out.println(result);
        assertThat(result.candidate).isEqualTo("the fast brown dog");
        assertThat(result.tokens).containsExactly("the", "quick", "brown", null, null, null, null, null, "dog");
    }

    @Test
    void testCandidatesWithWordsMissing() {
        Set<String> candidates = Set.of("hello world", "bronfos", "the fox jumps", "the fast brown dog");
        MatchResult result = StringCandidateMatcher.findBestMatch(candidates, "the quick brown fox jumps over the lazy dog");
        assertThat(result.candidate).isEqualTo("the fox jumps");
        assertThat(result.distance).isZero();
    }

    @Test
    void testMultipleMatchesLongestCandidate() {
        Set<String> candidates = Set.of("the quick", "lazy dog", "brown jumps the lazy", "over");
        MatchResult result = StringCandidateMatcher.findBestMatch(candidates, "the quick brown fox jumps over the lazy dog");
        assertThat(result.candidate).isEqualTo("brown jumps the lazy");
    }

    @Test
    void bankPurpose() {
        Set<String> candidates = Set.of("123 45 bob miller", "123 45 miller", "bob miller 123 45", "miller 123 45");
        String purpose = "transfer 123 45 31 45 for bobmiller";
        MatchResult result = StringCandidateMatcher.findBestMatch(candidates, purpose);
        assertThat(result.tokens).containsExactly(null, "123", "45", null, null, null, "bobmiller");
        assertThat(result.distance).isEqualTo(1);
    }
}
