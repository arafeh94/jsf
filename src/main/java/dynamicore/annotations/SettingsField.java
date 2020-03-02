package dynamicore.annotations;

import dynamicore.input.middlewares.DataInputMiddleware;
import dynamicore.xc_middlewares.Null;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface SettingsField {
    enum Category {
        FILTER, NAVIGATION, ANALYSIS, INPUT, NONE
    }

    String name();

    Class<?> assignedClass() default Null.class;

    String description();

    Category category();
}