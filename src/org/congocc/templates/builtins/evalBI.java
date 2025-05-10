package org.congocc.templates.builtins;

import org.congocc.templates.core.Environment;
import org.congocc.templates.core.nodes.generated.BuiltInExpression;
import org.congocc.templates.core.nodes.generated.Expression;
import org.congocc.templates.core.parser.CTemplatesLexer;
import org.congocc.templates.core.parser.CTemplatesParser;
import org.congocc.templates.core.parser.ParseException;
import org.congocc.templates.TemplateException;
import org.congocc.templates.core.variables.EvaluationException;

import static org.congocc.templates.core.variables.Wrap.asString;

/**
 * Implementation of ?eval built-in 
 */

public class evalBI extends ExpressionEvaluatingBuiltIn {

    @Override
    public Object get(Environment env, BuiltInExpression caller, Object model) 
    {
        try {
            return eval(asString(model), env, caller);
        } catch (ClassCastException cce) {
            throw new EvaluationException("Expecting string on left of ?eval built-in");

        } catch (NullPointerException npe) {
            throw new EvaluationException(npe);
        }
    }

    Object eval(String s, Environment env, BuiltInExpression caller) 
    {
        String input = "(" + s + ")";
        CTemplatesLexer token_source= new CTemplatesLexer("input", input, CTemplatesLexer.LexicalState.EXPRESSION, caller.getBeginLine(), caller.getBeginColumn());;
        CTemplatesParser parser = new CTemplatesParser(token_source);
        parser.setTemplate(caller.getTemplate());
        Expression exp = null;
        try {
            exp = parser.Expression();
        } catch (ParseException pe) {
            pe.setTemplateName(caller.getTemplate().getName());
            throw new TemplateException(pe, env);
        }
        return exp.evaluate(env);
    }
}