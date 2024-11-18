package ahud.adaptivehud.renderhud.element_values.attributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static ahud.adaptivehud.AdaptiveHUD.ATTRIBUTE_CLASSES;

public class AttributeParser {
    public AttributeResult parseAttributes(String[] attributes, Object value) {
        for (int i = 0; i < attributes.length; i++) {
            try {
                Class<?> customClass = ATTRIBUTE_CLASSES.get(value.getClass());
                if (customClass == null) {
                    return null;
                }

                Method method = customClass.getMethod(attributes[i]);
                Constructor<?> constructor = customClass.getDeclaredConstructor(value.getClass());
                value = method.invoke(constructor.newInstance(value));

                if (i == attributes.length - 1) {
                    return new AttributeResult(value, method);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
