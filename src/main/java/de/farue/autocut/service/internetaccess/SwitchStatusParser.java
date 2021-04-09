package de.farue.autocut.service.internetaccess;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class SwitchStatusParser {

    public Table<String, String, String> parse(String interfaceStatusString) {
        String[] lines = interfaceStatusString.split("\\R");
        int headerIndex = 0;
        for (; headerIndex < lines.length && StringUtils.isEmpty(lines[headerIndex]); headerIndex++) {}
        String header = lines[headerIndex];
        List<Integer> columnStartIndices = getColumnStartIndices(header);
        int numColumns = columnStartIndices.size();

        List<String> bodyLines = new ArrayList<>();
        for (int i = headerIndex + 1; i < lines.length; i++) {
            if (StringUtils.isNotEmpty(lines[i])) {
                bodyLines.add(lines[i]);
            }
        }

        List<String> rowKeys = bodyLines
            .stream()
            .map(line -> getCellAt(line, columnStartIndices.get(0), columnStartIndices.get(1)))
            .collect(Collectors.toList());
        List<String> columnKeys = getCells(header, columnStartIndices);

        @SuppressWarnings("UnstableApiUsage")
        Table<String, String, String> table = ArrayTable.create(rowKeys, columnKeys);
        for (int i = 0; i < bodyLines.size(); i++) {
            String line = bodyLines.get(i);
            List<String> cells = getCells(line, columnStartIndices);
            if (cells.size() != columnKeys.size()) {
                throw new RuntimeException(
                    "Expected " + columnKeys.size() + " cells in row " + i + " but was " + cells.size() + ": " + cells
                );
            }
            for (int j = 0; j < cells.size(); j++) {
                table.put(rowKeys.get(i), columnKeys.get(j), cells.get(j));
            }
        }
        return table;
    }

    private List<String> getCells(String line, List<Integer> columnStartIndices) {
        List<String> cells = new ArrayList<>();
        List<Integer> wordBoundaries = new ArrayList<>(columnStartIndices);
        wordBoundaries.add(line.length());
        for (int i = 0; i < wordBoundaries.size() - 1; i++) {
            cells.add(getCellAt(line, wordBoundaries.get(i), wordBoundaries.get(i + 1)));
        }
        return cells;
    }

    private String getCellAt(String line, int columnStartIndex, int columnEndIndex) {
        return getWord(line, columnStartIndex, columnEndIndex);
    }

    private List<Integer> getColumnStartIndices(String header) {
        List<Integer> columnIndices = new ArrayList<>();
        char[] chars = header.toCharArray();
        boolean newWord = true;
        for (int i1 = 0; i1 < chars.length; i1++) {
            if (newWord && !Character.isWhitespace(chars[i1])) {
                columnIndices.add(i1);
                newWord = false;
            } else if (Character.isWhitespace(chars[i1])) {
                newWord = true;
            }
        }
        return columnIndices;
    }

    private String getWord(String line, int start, int end) {
        String word = StringUtils.substring(line, start, end);
        return StringUtils.trimToNull(word);
    }
}
