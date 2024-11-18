package ahud.adaptivehud.renderhud.element_values.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(SetDefaultGlobalFlagCont.class)
public @interface SetDefaultGlobalFlag {
    String flag();
    String[] values() default {};
}