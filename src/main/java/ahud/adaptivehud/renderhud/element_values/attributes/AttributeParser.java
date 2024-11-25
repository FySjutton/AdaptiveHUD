package ahud.adaptivehud.renderhud.element_values.attributes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static ahud.adaptivehud.AdaptiveHUD.ATTRIBUTE_CLASSES;

public class AttributeParser {
    public AttributeResult parseAttributes(String[] attributes, Object value) {
        for (int i = 0; i < attributes.length; i++) {
            try {
                Class<?> valueClass = findAttributeClass(value.getClass());
                Class<?> customClass = ATTRIBUTE_CLASSES.get(valueClass);
                if (customClass == null) {
                    return null;
                }

                Method method = customClass.getMethod(attributes[i]);
                Constructor<?> constructor = customClass.getDeclaredConstructor(valueClass);
                value = method.invoke(constructor.newInstance(value));

                if (i == attributes.length - 1) {
                    return new AttributeResult(value, method);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private Class<?> findAttributeClass(Class<?> clazz) {
        while (clazz != null) {
            Class<?> customClass = ATTRIBUTE_CLASSES.get(clazz);
            if (customClass != null) {
                return clazz;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
