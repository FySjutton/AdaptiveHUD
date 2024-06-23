package ahud.adaptivehud.renderhud.variables;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AttributeName {
    String value();
}