package ahud.adaptivehud.renderhud.variables.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SetDefaultGlobalFlagCont {
    SetDefaultGlobalFlag[] value();  // An array to hold multiple @SetDefaultFlag annotations
}