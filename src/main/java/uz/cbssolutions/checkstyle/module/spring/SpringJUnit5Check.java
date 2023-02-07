/*
 * Copyright 2017-2020 the original author or authors.
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

package uz.cbssolutions.checkstyle.module.spring;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import uz.cbssolutions.checkstyle.module.AbstractSpringCheck;

import java.util.*;

/**
 * Checks that JUnit 5 conventions are followed and that JUnit 4 is not accidentally used.
 *
 * @author Phillip Webb
 */
public class SpringJUnit5Check extends AbstractSpringCheck {

    private static final String JUNIT4_TEST_ANNOTATION = "org.junit.Test";

    private static final List<String> TEST_ANNOTATIONS;
    private static final List<String> LIFECYCLE_ANNOTATIONS;
    private static final Set<String> BANNED_IMPORTS;

    static {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add("RepeatedTest");
        annotations.add("Test");
        annotations.add("TestFactory");
        annotations.add("TestTemplate");
        annotations.add("ParameterizedTest");
        TEST_ANNOTATIONS = Collections.unmodifiableList(new ArrayList<>(annotations));
    }

    static {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add("BeforeAll");
        annotations.add("BeforeEach");
        annotations.add("AfterAll");
        annotations.add("AfterEach");
        LIFECYCLE_ANNOTATIONS = Collections.unmodifiableList(new ArrayList<>(annotations));
    }

    static {
        Set<String> bannedImports = new LinkedHashSet<>();
        bannedImports.add(JUNIT4_TEST_ANNOTATION);
        bannedImports.add("org.junit.After");
        bannedImports.add("org.junit.AfterClass");
        bannedImports.add("org.junit.Before");
        bannedImports.add("org.junit.BeforeClass");
        bannedImports.add("org.junit.Rule");
        bannedImports.add("org.junit.ClassRule");
        BANNED_IMPORTS = Collections.unmodifiableSet(bannedImports);
    }

    private final List<DetailAST> testMethods = new ArrayList<>();
    private final Map<String, FullIdent> imports = new LinkedHashMap<>();
    private final List<DetailAST> lifecycleMethods = new ArrayList<>();
    private final List<String> unlessImports = new ArrayList<>();

    @Override
    public int[] getAcceptableTokens() {
        return new int[]{TokenTypes.METHOD_DEF, TokenTypes.IMPORT};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        this.imports.clear();
        this.testMethods.clear();
        this.lifecycleMethods.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
                visitMethodDef(ast);
            case TokenTypes.IMPORT:
                visitImport(ast);
                break;
        }
    }

    private void visitMethodDef(DetailAST ast) {
        if (AnnotationUtil.containsAnnotation(ast, TEST_ANNOTATIONS)) {
            this.testMethods.add(ast);
        }
        if (AnnotationUtil.containsAnnotation(ast, LIFECYCLE_ANNOTATIONS)) {
            this.lifecycleMethods.add(ast);
        }
    }

    private void visitImport(DetailAST ast) {
        FullIdent ident = FullIdent.createFullIdentBelow(ast);
        this.imports.put(ident.getText(), ident);
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        if (shouldCheck()) {
            check();
        }
    }

    private boolean shouldCheck() {
        if (this.testMethods.isEmpty() && this.lifecycleMethods.isEmpty()) {
            return false;
        }
        for (String unlessImport : this.unlessImports) {
            if (this.imports.containsKey(unlessImport)) {
                return false;
            }
        }
        return true;
    }

    private void check() {
        for (String bannedImport : BANNED_IMPORTS) {
            FullIdent ident = this.imports.get(bannedImport);
            if (ident != null) {
                log(ident.getLineNo(), ident.getColumnNo(), "Banned imports usage. Use Junit 5", bannedImport);
            }
        }
        for (DetailAST testMethod : this.testMethods) {
            if (AnnotationUtil.containsAnnotation(testMethod, JUNIT4_TEST_ANNOTATION)) {
                log(testMethod, "Banned test annotation usage. Use Junit 5");
            }
        }
        checkMethodVisibility(this.testMethods, "Public modifier is not allowed. Test methods of Junit 5 must be package-private", "Private modifier is not allowed. Test methods of Junit 5 must be package-private");
        checkMethodVisibility(this.lifecycleMethods, "Public modifier is not allowed. Lifecycle methods of Junit 5 must be package-private", "Private modifier is not allowed. Lifecycle methods of Junit 5 must be package-private");
    }

    private void checkMethodVisibility(List<DetailAST> methods, String publicMessageKey, String privateMessageKey) {
        for (DetailAST method : methods) {
            DetailAST modifiers = method.findFirstToken(TokenTypes.MODIFIERS);
            if (modifiers.findFirstToken(TokenTypes.LITERAL_PUBLIC) != null) {
                log(method, publicMessageKey);
            }
            if (modifiers.findFirstToken(TokenTypes.LITERAL_PRIVATE) != null) {
                log(method, privateMessageKey);
            }
        }
    }

    private void log(DetailAST method, String key) {
        String name = method.findFirstToken(TokenTypes.IDENT).getText();
        log(method.getLineNo(), method.getColumnNo(), key, name);
    }
}
