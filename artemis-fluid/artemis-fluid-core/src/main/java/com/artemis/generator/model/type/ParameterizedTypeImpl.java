package com.artemis.generator.model.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Daan van Yperen
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    private Type rawType;
    private Type[] arguments;

    public ParameterizedTypeImpl(Type rawType, Type... arguments) {
        this.rawType = rawType;
        this.arguments = arguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return arguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append(this.rawType);

        if (this.arguments != null && this.arguments.length > 0) {
            var1.append("<");
            boolean var2 = true;
            Type[] var3 = this.arguments;
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Type var6 = var3[var5];
                if (!var2) {
                    var1.append(", ");
                }

                var1.append(var6);
                var2 = false;
            }

            var1.append(">");
        }

        return var1.toString();
    }

}
