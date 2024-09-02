package ahud.adaptivehud.renderhud.variables.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(SetDefaultGlobalFlagCont.class)  // Specify the container annotation
public @interface SetDefaultGlobalFlag {
    String flag();
    String value() default "";
}