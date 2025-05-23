package org.congocc.templates.builtins;

import org.congocc.templates.core.Environment;
import org.congocc.templates.core.nodes.generated.BuiltInExpression;
import org.congocc.templates.core.nodes.generated.TemplateNode;
import org.congocc.templates.TemplateHashModel;

import static org.congocc.templates.core.variables.Wrap.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * Implementation of ?resolve built-in 
 */

public abstract class HashBuiltin extends ExpressionEvaluatingBuiltIn {

    @Override
    public Object get(Environment env, BuiltInExpression caller, Object lhs) 
    {
        if (!(lhs instanceof TemplateHashModel) && !isMap(lhs)) {
            throw TemplateNode.invalidTypeException(lhs, 
                    caller.getTarget(), env, "hash");
        }
        return apply(unwrap(lhs));
    }
    
    public abstract Iterable apply(Object hash); 
    
    public static class Keys extends HashBuiltin {
        @Override
        public Iterable apply(Object hash) {
            if (hash instanceof Map) {
                return new ArrayList(((Map)hash).keySet());
            }
            return ((TemplateHashModel) hash).keys();
        }
    }

    public static class Values extends HashBuiltin {
        @Override
        public Iterable apply(Object hash)
        {
            if (hash instanceof TemplateHashModel) {
                return ((TemplateHashModel)hash).values();
            }
            return new ArrayList(((Map) hash).values());
        }
    }
}
