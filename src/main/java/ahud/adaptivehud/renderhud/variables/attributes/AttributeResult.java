package ahud.adaptivehud.renderhud.variables.attributes;

import java.lang.reflect.Method;

public class AttributeResult {
    private final Object value;
    private final Method method;

    public AttributeResult(Object value, Method method) {
        this.value = value;
        this.method = method;
    }

    public Object getValue() {
        return value;
    }

    public Method getMethod() {
        return method;
    }
}
