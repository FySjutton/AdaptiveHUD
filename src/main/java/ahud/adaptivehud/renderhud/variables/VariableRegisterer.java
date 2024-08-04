package ahud.adaptivehud.renderhud.variables;

import ahud.adaptivehud.renderhud.variables.inbuilt_variables.DefaultVariables;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static ahud.adaptivehud.adaptivehud.LOGGER;

public class VariableRegisterer {
    private static final Map<String, Method> variableList = new HashMap<>();

    public void registerVariable(String name, Method method) {
//        Class<?>[] parameters = method.getParameterTypes();
//        if (parameters.co)
//        if (parameters == 2) {
        variableList.put(name, method);
//        } else {
//            LOGGER.error("Variable {} could not be registered because it doesn't have the right parameters.", name);
//        }
    }

    public Method loadVariable(String name) {
        try {
            return variableList.get(name);
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
        variableList.clear();
    }
}
