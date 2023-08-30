/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.israiloff.checkstyle.module.spring;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import io.github.israiloff.checkstyle.module.AbstractSpringCheck;

/**
 * Checks that lambda definitions follow Spring conventions. Single argument lambda
 * parameters should have parentheses. Single statement implementations should not use
 * curly braces.
 *
 * @author Phillip Webb
 */
public class SpringLambdaCheck extends AbstractSpringCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getAcceptableTokens() {
        return new int[]{TokenTypes.LAMBDA};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.LAMBDA && ast.getParent() != null
                && ast.getParent().getType() != TokenTypes.SWITCH_RULE) {
            visitLambda(ast);
        }
    }

    private void visitLambda(DetailAST lambda) {
        DetailAST block = lambda.getLastChild();
        int statements = countDescendantsOfType(block, TokenTypes.SEMI);
        int requireBlock = countDescendantsOfType(block, TokenTypes.LCURLY, TokenTypes.LITERAL_THROW, TokenTypes.SLIST);
        if (statements == 1 && requireBlock == 0) {
            log(block.getLineNo(), block.getColumnNo(), "Unnecessary block in lambda expression");
        }
    }

    private int countDescendantsOfType(DetailAST ast, int... types) {
        int count = 0;
        for (int type : types) {
            count += ast.getChildCount(type);
        }
        DetailAST child = ast.getFirstChild();
        while (child != null) {
            count += countDescendantsOfType(child, types);
            child = child.getNextSibling();
        }
        return count;
    }
}
