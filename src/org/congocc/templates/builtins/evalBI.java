package org.congocc.templates.builtins;

import org.congocc.templates.core.Environment;
import org.congocc.templates.core.nodes.generated.BuiltInExpression;
import org.congocc.templates.core.nodes.generated.Expression;
import org.congocc.templates.core.parser.CTemplatesLexer;
import org.congocc.templates.core.parser.CTemplatesParser;

public class evalBI extends ExpressionEvaluatingBuiltIn {

    @Override
    public Object get(Environment env, BuiltInExpression caller, Object model) {
        String input = "(" + model + ")";
        CTemplatesLexer token_source= new CTemplatesLexer("input", input, CTemplatesLexer.LexicalState.EXPRESSION, caller.getBeginLine(), caller.getBeginColumn());;
        CTemplatesParser parser = new CTemplatesParser(token_source);
        parser.setTemplate(caller.getTemplate());
        Expression exp = parser.Expression();
        return exp.evaluate(env);
    }
}