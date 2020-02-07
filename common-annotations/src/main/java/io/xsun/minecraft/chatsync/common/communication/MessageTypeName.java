package io.xsun.minecraft.chatsync.common.communication;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MessageTypeName {
    String value();
}
