package org.congocc.templates.core.variables;

import java.util.*;
import java.lang.reflect.Array;
import org.congocc.templates.core.Environment;
import org.congocc.templates.core.nodes.generated.Expression;
import org.congocc.templates.TemplateBooleanModel;
import org.congocc.templates.TemplateException;
import org.congocc.templates.TemplateSequenceModel;

public class Wrap {
    /**
     * A general-purpose object to represent nothing. It acts as
     * an empty string, false, empty sequence, empty hash, and
     * null-returning method. It is useful if you want
     * to simulate typical loose scripting language sorts of 
     * behaviors in your templates. 
     */
    public static final Object NOTHING = GeneralPurposeNothing.getInstance();

    /**
     * A singleton value used to represent a java null
     * which comes from a wrapped Java API, for example, i.e.
     * is intentional. A null that comes from a generic container
     * like a map is assumed to be unintentional and a 
     * result of programming error.
     */
    public static final Object JAVA_NULL = new JavaNull(); 
    
    static private class JavaNull implements WrappedVariable {
        public Object getWrappedObject() {
            return null;
        }
    }

    private static final Class<?> RECORD_CLASS;

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("java.lang.Record");
        } catch (Exception e) {
        }
        finally {
            RECORD_CLASS = clazz;
        }
    }

    public static boolean isJdk14OrGreater() {
        return RECORD_CLASS != null;
    }

    private Wrap() {}

    public static boolean isRecord(Object obj) {
        return RECORD_CLASS != null && RECORD_CLASS.isInstance(obj);
    }

    public static boolean isMap(Object obj) {
        if (obj instanceof WrappedVariable) {
            obj = ((WrappedVariable) obj).getWrappedObject();
        }
        return obj instanceof Map;
    }

    public static boolean isList(Object obj) {
        if (obj instanceof TemplateSequenceModel) {
            return true;
        }
        if (obj.getClass().isArray()) {
            return true;
        }
        return obj instanceof List;
    }

    public static List<?> asList(Object obj) {
        if (obj instanceof TemplateSequenceModel) {
            TemplateSequenceModel tsm = (TemplateSequenceModel) obj;
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < tsm.size(); i++)
                result.add(tsm.get(i));
            return result;
        }
        if (obj.getClass().isArray()) {
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < Array.getLength(obj); i++) {
                result.add(Array.get(obj, i));
            }
            return result;
        }
        return (List<?>) obj;
    }

    public static String asString(Object obj) {
        return obj.toString();
    }

    public static boolean isBoolean(Object obj) {
        if (obj instanceof TemplateBooleanModel) {
            return true;
        }
        if (obj instanceof WrappedVariable) {
            obj = ((WrappedVariable) obj).getWrappedObject();
        }
        return obj instanceof Boolean;
    }

    public static boolean asBoolean(Object obj) {
        if (obj instanceof TemplateBooleanModel) {
            return ((TemplateBooleanModel) obj).getAsBoolean();
        }
        return (Boolean) obj;
    }

    public static boolean isIterable(Object obj) {
        return obj instanceof Iterable 
              || obj instanceof Iterator 
              || obj.getClass().isArray();
    }

    public static Iterator<?> asIterator(Object obj) {
        if (obj instanceof Iterator) {
            return (Iterator<?>) obj;
        }
        if (obj.getClass().isArray()) {
            final Object arr = obj;
            return new Iterator<Object>() {
                int index = 0;

                public boolean hasNext() {
                    return index < Array.getLength(arr);
                }

                public Object next() {
                    return Array.get(arr, index++);
                }
            };
        }
        return ((Iterable<?>) obj).iterator();
    }

    public static Object wrap(Object object) {
        if (object == null) {
            return JAVA_NULL;
        }
        if (object instanceof ResourceBundle) {
            return new ResourceBundleWrapper((ResourceBundle) object);
        }
        return object;
    }

    public static Object unwrap(Object object) {
        if (object == null) {
            throw new EvaluationException("invalid reference");
        }
        if (object == JAVA_NULL) {
            return null;
        }
        if (object instanceof WrappedVariable) {
            Object unwrapped = ((WrappedVariable) object).getWrappedObject();
            if (unwrapped !=null) {
                return unwrapped;
            }
        }
        return object;
    }

    static public Number getNumber(Object object, Expression expr, Environment env)
    {
        if(object instanceof Number) {
            return (Number) object;
        }
        else if(object == null) {
            throw new InvalidReferenceException(expr + " is undefined.", env);
        }
        else if(object == JAVA_NULL) {
            throw new InvalidReferenceException(expr + " is null.", env);
        }
        else {
            throw new TemplateException(expr + " is not a number, it is " + object.getClass().getName(), env);
        }
    }

    static public Number getNumber(Expression expr, Environment env)
    {
        Object value = expr.evaluate(env);
        return getNumber(value, expr, env);
    }
}