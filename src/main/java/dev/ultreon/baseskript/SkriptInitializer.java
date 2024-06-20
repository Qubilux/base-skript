package dev.ultreon.baseskript;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SkriptInitializer {
	Class<?>[] value();
}
