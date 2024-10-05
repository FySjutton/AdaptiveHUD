package ahud.adaptivehud;

import ahud.adaptivehud.renderhud.variables.inbuilt_variables.DefaultVariables;

import java.lang.reflect.Method;
import java.util.Set;

import static ahud.adaptivehud.AdaptiveHUD.*;

public class AdaptiveHudRegistry {

    public void registerVariable(String name, Method method, boolean overwrite) {
        if (VARIABLES.containsKey(name) && !overwrite) {
            return;
        }
        VARIABLES.put(name, method);
    }

    public boolean unregisterVariable(String name) {
        boolean contains = VARIABLES.containsKey(name);
        VARIABLES.remove(name);
        return contains;
    }

    public boolean hasVariable(String name) {
        return VARIABLES.containsKey(name);
    }

    public Set<String> variableList() {
        return VARIABLES.keySet();
    }

    public Method loadVariable(String name) {
        try {
            return VARIABLES.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean registerFlag(String name, Method method, boolean overwrite) {
        if (FLAGS.containsKey(name) && !overwrite) {
            return false;
        }
        if (method.getParameters().length == 2) {
            LOGGER.info(String.valueOf(method.getParameters()[0].getType()));
            LOGGER.info(String.valueOf(method.getParameters()[1].getType()));
            if (method.getParameters()[0].getType() == String.class && method.getParameters()[1].getType() == String[].class) {
                FLAGS.put(name, method);
                return true;
            }
        }
        return false;
    }

    public boolean unregisterFlag(String name) {
        boolean contains = FLAGS.containsKey(name);
        FLAGS.remove(name);
        return contains;
    }

    public boolean hasFlag(String name) {
        return FLAGS.containsKey(name);
    }

    public Set<String> flagList() {
        return FLAGS.keySet();
    }

    public Method loadFlag(String name) {
        try {
            return FLAGS.get(name);
        } catch (Exception e) {
            return null;
        }
    }
}
