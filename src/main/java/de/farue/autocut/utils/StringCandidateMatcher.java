package de.farue.autocut.utils;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;

public class StringCandidateMatcher {

    // 22 tokens took about 25 seconds. Every single additional token would double the computation time.
    private static final int MAX_NUMBER_TOKENS = 22;

    /**
     * This function finds the parts of the sentence and a single candidate that match best
     * according to Levenshtein distance.
     *
     * The candidates are considered the "source of truth" in a sense that matching any of them
     * perfectly is considered a perfect match. The sentence is split into words and the function
     * tries leaving out words to obtain a better match. Words are only left out, not swapped.
     *
     * If multiple candidates match equally well, the longest one is returned.
     *
     * @param matchCandidates the candidates to be matched by a subset of words of the sentence
     * @param sentence the string to find one of the candidates
     * @return match result
     */
    public static MatchResult findBestMatch(Collection<String> matchCandidates, String sentence) {
        String[] tokens = tokenize(sentence);
        return matchCandidates.stream()
            .sorted((s1, s2) -> s2.length() - s1.length())
            .map(candidate -> match(candidate, tokens))
            .max(MatchResult::compareTo)
            .orElseThrow(() -> new IllegalArgumentException("Candidates must not be empty"));
    }

    private static MatchResult match(String candidate, String[] tokens) {
        MatchResult initialMatch = calculateMatchResult(candidate, tokens);
        if (initialMatch.distance == 0) {
            return initialMatch;
        }

        // prevent indefinite calculation time
        if (tokens.length > MAX_NUMBER_TOKENS) {
            return initialMatch;
        }

        //noinspection ConstantConditions
        long maxBitmask = tokens.length == 64 ? Long.MAX_VALUE : (1 << tokens.length) - 1;
        MatchResult bestMatch = initialMatch;
        // strictly smaller is sufficient because the case 111...1 is handled by initialMatch
        for (long bitmask = 0; bitmask < maxBitmask; bitmask++) {
            String[] maskedTokens = copyMasked(tokens, bitmask);
            MatchResult matchResult = calculateMatchResult(candidate, maskedTokens);

            if (matchResult.distance == 0) {
                // perfect match found for this candidate
                return matchResult;
            }

            if (bestMatch.errorRatio > matchResult.errorRatio) {
                bestMatch = matchResult;
            }
        }

        return bestMatch;
    }

    private static MatchResult calculateMatchResult(String candidate, String[] tokens) {
        String joinedTokens = Arrays.stream(tokens).filter(Objects::nonNull).collect(Collectors.joining(" "));
        int distance = levenshtein(candidate, joinedTokens);

        int length = StringUtils.length(joinedTokens);
        double errorRatio = length == 0 ? Double.MAX_VALUE :  (double) distance / length;

        return new MatchResult(candidate, tokens, errorRatio, distance);
    }

    private static String[] copyMasked(String[] tokens, long bitmask) {
        String[] t = new String[tokens.length];
        BitSet bitSet = BitSet.valueOf(new long[]{bitmask});
        for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
            t[i] = tokens[i];
        }
        return t;
    }

    private static String[] tokenize(String purpose) {
        return StringUtils.split(purpose);
    }

    private static int levenshtein(String s1, String s2) {
        return Levenshtein.distance(s1, s2);
    }

    @AllArgsConstructor
    public static class MatchResult implements Comparable<MatchResult> {
        public final String candidate;
        public final String[] tokens;
        public final double errorRatio;
        public final int distance;

        @Override
        public int compareTo(MatchResult o) {
            // lower error ratio should give positive result
            return Double.compare(o.errorRatio, errorRatio);
        }

        @Override
        public String toString() {
            return "MatchResult{" +
                "candidate='" + candidate + '\'' +
                ", tokens=" + Arrays.toString(tokens) +
                ", errorRatio=" + errorRatio +
                ", distance=" + distance +
                '}';
        }
    }
}
