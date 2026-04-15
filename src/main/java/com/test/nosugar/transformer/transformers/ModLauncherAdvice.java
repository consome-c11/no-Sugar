package com.test.nosugar.agent.transformer.transformers;

/*import com.test.nosugar.transformer.NSBootstrap;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.matcher.ElementMatchers;

public class ModLauncherAdvice {

    public static AsmVisitorWrapper.ForDeclaredMethods getAdvice() {
        return Advice.to(ModLauncherAdvice.class)
                .on(ElementMatchers.named("transform")
                        .and(ElementMatchers.takesArguments(byte[].class, String.class, String.class)));
    }

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static void enter(
            @Advice.Argument(value = 0, readOnly = false) byte[] inputClass,
            @Advice.Argument(1) String className,
            @Advice.Argument(2) String reason,
            @Advice.Local("original") byte[] original
    ) {
        try {
            if (inputClass == null || inputClass.length == 0) {
                return;
            }
            if (className == null || className.isEmpty()) return;
            String internalName = className.replace('.', '/');
            if (internalName.startsWith("com/test/nosugar/transformer")) return;
            original = inputClass;
            byte[] transformed = NSBootstrap.dispatch(internalName, inputClass, reason);
            if (transformed != null && transformed != inputClass) {
                inputClass = transformed;
            }

        } catch (Throwable t) {
            System.out.println("Failed to transform: " + t);
        }
    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    static void exit(
            @Advice.Return(readOnly = false) byte[] result,
            @Advice.Local("original") byte[] original
    ) {
        if (result == null && original != null) {
            result = original;
        }
    }
}*/