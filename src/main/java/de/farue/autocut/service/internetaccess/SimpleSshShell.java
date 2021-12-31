package de.farue.autocut.service.internetaccess;

import java.io.IOException;
import java.util.regex.Pattern;
import net.sf.expectit.Expect;
import net.sf.expectit.Result;
import net.sf.expectit.matcher.Matchers;
import org.apache.commons.lang3.StringUtils;

public class SimpleSshShell implements SshShell {

    private final Expect expect;
    private String prompt;

    public SimpleSshShell(Expect expect) {
        this.expect = expect;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void autoDetectPrompt(String promptSymbol) throws IOException {
        synchronized (expect) {
            Result result = expect.expect(Matchers.contains(promptSymbol));
            prompt = (result.getBefore() + promptSymbol).strip();
        }
    }

    @Override
    public String execute(String exec) throws IOException {
        synchronized (expect) {
            expect.sendLine(exec);
            Result result = this.expect.expect(Matchers.regexp(Pattern.compile(prompt, Pattern.MULTILINE)));
            String output = result.getBefore();
            return StringUtils.removeStart(output, exec);
        }
    }

    @Override
    public void close() throws IOException {
        expect.close();
    }
}
