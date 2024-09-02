package ahud.adaptivehud.renderhud.variables.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(SetDefaultFlagCont.class)  // Specify the container annotation
public @interface SetDefaultFlag {
    String flag();
    String value() default "";
}