package io.kayt.refluent.core.ui.theme.typography;


import android.graphics.Typeface;
import android.util.Log;

import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.platform.AndroidTypeface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class AndroidTypefaceProxy {
    @SuppressWarnings("unchecked")
    public static AndroidTypeface create(Typeface base) {
        final var cl = AndroidTypeface.class.getClassLoader();
        final var ifaces = new Class<?>[]{AndroidTypeface.class};

        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();

            // The string is mangled by kotlin to avoid using in java as the class is internal
            // So it might change in the next version of library so I used "startWith"
            if (name.startsWith("getNativeTypeface")) {
                FontWeight weight = (FontWeight) args[0];
                int style = (int) args[1];
                int synth = (int) args[2];

                // Map FontStyle to Typeface style flag
                int styleFlag = (style == 0) ? Typeface.NORMAL : Typeface.ITALIC;

                int w = weight.getWeight(); // Kotlin value class exposes getter in Java
                Typeface result;
                result = Typeface.create(base, w, styleFlag == Typeface.ITALIC);
                return result;
            } else if ("hashCode".equals(name)) {
                return base.hashCode() + 13;
            }
            throw new UnsupportedOperationException("Method not handled: " + name);
        };

        return (AndroidTypeface) Proxy.newProxyInstance(cl, ifaces, handler);
    }
}
