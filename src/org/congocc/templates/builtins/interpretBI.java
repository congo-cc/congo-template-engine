package org.congocc.templates.builtins;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.List;

import org.congocc.templates.core.Environment;
import org.congocc.templates.core.nodes.generated.BuiltInExpression;
import org.congocc.templates.Template;
import org.congocc.templates.core.variables.UserDirectiveBody;
import org.congocc.templates.core.variables.UserDirective;
import org.congocc.templates.core.variables.EvaluationException;
import org.congocc.templates.core.variables.InvalidReferenceException;
import org.congocc.templates.TemplateException;

import static org.congocc.templates.core.variables.Wrap.*;

/**
 * Implementation of ?interpret built-in 
 */
public class interpretBI extends ExpressionEvaluatingBuiltIn {

    @Override
    public Object get(Environment env, BuiltInExpression caller, Object model) 
    {
        String id = null, interpretString = null;
        if (isList(model)) {
            List tsm = asList(model);
            Object tm = tsm.size() >1 ? tsm.get(1) : null;
            if (tm != null) {
                if (tm instanceof CharSequence) {
                    id = asString(tm);
                }
                else {
                    throw new EvaluationException("Expecting string as second item of sequence of left of ?interpret built-in");
                }
            }
            tm = tsm.get(0);
            if (!(tm instanceof CharSequence)) {
                throw new EvaluationException("Expecting string as first item of sequence of left of ?interpret built-in");
            }
            interpretString = asString(tm);
        }
        else if (model instanceof CharSequence) {
            interpretString = asString(model);
        }
        if (id == null) id = "anonymous_interpreted";
        if (interpretString == null) {
            throw new InvalidReferenceException("No string to interpret", env);
        }
        Template parentTemplate = env.getTemplate();
        try {
            Template template = new Template(parentTemplate.getName() + "$" + id, interpretString, parentTemplate.getConfiguration(), parentTemplate.getEncoding());
            template.setLocale(env.getLocale());
            return new TemplateProcessorModel(template);
        }
        catch(IOException e) {
            throw new TemplateException("", e, env);
        }
    }

    private static class TemplateProcessorModel implements UserDirective {
        private final Template template;

        TemplateProcessorModel(Template template) {
            this.template = template;
        }

        public void execute(Environment env, Map<String, Object> params, Object[] loopVars, UserDirectiveBody body) throws IOException {
            try {
                env.include(template, false);
            }
            catch(RuntimeException e) {
                throw e;
            }
            catch(IOException e) {
                throw e;
            }
            catch(Exception e) {
                throw new EvaluationException(e);
            }
        }
    }
}
