package uz.cbssolutions.checkstyle.module.cbs;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import uz.cbssolutions.checkstyle.module.AbstractSpringCheck;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Spring bean name validation check. Bean name must be specified in camelcase.
 */
public class CbsBeanNameCheck extends AbstractSpringCheck {

    private final Pattern pattern = Pattern.compile("^\"([a-z])\\w+\"$");
    private final String[] annotations = new String[]{"Bean", "org.springframework.context.annotation.Bean"};

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getAcceptableTokens() {
        return new int[]{TokenTypes.ANNOTATION};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leaveToken(DetailAST ast) {
        if (containsSibling(ast.getFirstChild(), TokenTypes.IDENT, annotations, ast.getChildCount())
                && isNotMatchesPattern(ast)) {
            log(ast.getLineNo(), ast.getColumnNo(), "Bean name must be specified in camelcase");
        }
    }

    private boolean isNotMatchesPattern(DetailAST ast) {

        var parent = getChild(ast.getFirstChild(), TokenTypes.EXPR, ast.getChildCount());

        if (parent == null) {
            return false;
        }

        var child = getChild(parent.getFirstChild(), TokenTypes.STRING_LITERAL, parent.getChildCount());

        return child != null && !pattern.matcher(child.getText()).matches();
    }

    private boolean containsSibling(DetailAST ast, int type, String[] texts, int count) {

        if (ast == null || count < 1) {
            return false;
        }

        if (ast.getType() == type && Arrays.stream(texts).anyMatch(s -> Objects.equals(ast.getText(), s))) {
            return true;
        }

        return containsSibling(ast.getNextSibling(), type, texts, --count);
    }

    private DetailAST getChild(DetailAST ast, int type, int count) {

        if (ast == null || count < 1) {
            return null;
        }

        if (ast.getType() == type) {
            return ast;
        }

        return getChild(ast.getNextSibling(), type, --count);
    }
}
