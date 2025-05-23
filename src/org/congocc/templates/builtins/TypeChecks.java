package org.congocc.templates.builtins;

import org.congocc.templates.core.Environment;
import org.congocc.templates.core.nodes.generated.BuiltInExpression;
import org.congocc.templates.core.nodes.generated.Macro;
import org.congocc.templates.core.variables.*;
import org.congocc.templates.TemplateHashModel;

import static org.congocc.templates.core.variables.Wrap.*;

/**
 * Implementation of ?is_XXXX built-ins
 * TODO: refactor into subclasses
 */

public class TypeChecks extends ExpressionEvaluatingBuiltIn {
	
    @Override
    public Object get(Environment env, BuiltInExpression caller, Object value) {
		boolean result = false;
		final String builtInName = caller.getName(); 
		if (builtInName == "is_string") {
			result = value instanceof CharSequence;
		}
		else if (builtInName == "is_number") {
			result = value instanceof Number;
		}
		else if (builtInName == "is_enumerable" || builtInName == "is_collection") {
			result = isIterable(value);
		}
		else if (builtInName == "is_sequence" || builtInName == "is_indexable") {
			result = isList(value);
		}
		else if (builtInName == "is_macro") {
			result = (value instanceof Macro) && !((Macro) value).isFunction();
		}
		else if (builtInName == "is_directive") {
			result = value instanceof Macro || value instanceof UserDirective;
		}
		else if (builtInName == "is_boolean") {
			result = isBoolean(value);
		}
		else if (builtInName == "is_hash") {
			result = value instanceof TemplateHashModel;
		}
		else if (builtInName == "is_hash_ex") {
			result = value instanceof TemplateHashModel;
		}
		else if (builtInName == "is_method") {
			result = value instanceof VarArgsFunction;
		}
		else if (builtInName == "is_null") {
			result = value == JAVA_NULL;
		}
		else if (builtInName == "is_transform") {
			result = false;
		}
		return result;
	}
}
