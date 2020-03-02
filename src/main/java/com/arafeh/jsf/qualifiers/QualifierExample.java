package com.arafeh.jsf.qualifiers;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * this kind of qualifiers is used when we have multiple implementation of the
 * same service.
 * use it like this
 * set default qualifier in class creation:
 * @Stateless
 * @QualifierExample(QualifierExample.QualifierType.FIRST)
 * public class SomeService implements ServiceInterface
 *
 * set it while creating new instance that depends on Inject
 * or EJB auto creation
 * @Inject
 * @QualifierExample(QualifierExample.QualifierType.FIRST)
 * TwitterService twitterService;
 *
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, TYPE, METHOD})
public @interface QualifierExample {
    QualifierType value();

    enum QualifierType {
        FIRST, SECOND
    }
}
