package ahud.adaptivehud.renderhud.variables;

import ahud.adaptivehud.renderhud.variables.inbuilt_variables.DefaultVariables;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class VariableRegisterer {
    private static final Map<String, Method> VARIABLE_LIST = new HashMap<>();

    public void registerVariable(String name, Method method) {
//        Class<?>[] parameters = method.getParameterTypes();
//        if (parameters.co)
//        if (parameters == 2) {
        VARIABLE_LIST.put(name, method);
//        } else {
//            LOGGER.error("Variable {} could not be registered because it doesn't have the right parameters.", name);
//        }
    }

    public Method loadVariable(String name) {
        try {
            return VARIABLE_LIST.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    public void registerDefaults() {
        try {
            for (Method method : DefaultVariables.class.getDeclaredMethods()) {
                registerVariable(method.getName(), method);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load a default variable!");
            LOGGER.error(e.toString());
        }
    }

    public void deleteVariables() {
        VARIABLE_LIST.clear();
    }
}
