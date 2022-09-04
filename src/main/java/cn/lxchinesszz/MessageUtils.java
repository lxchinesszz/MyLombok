package cn.lxchinesszz;

import org.slf4j.helpers.MessageFormatter;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.Objects;

/**
 * @author liuxin
 * 2022/9/2 19:23
 */
public class MessageUtils {

    private Messager messager;

    private MessageUtils(Messager messager) {
        this.messager = messager;
    }

    private MessageUtils() {
    }


    public static MessageUtils getMessageUtils(Messager messager) {
        return new MessageUtils(messager);
    }


    public void info(String format, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, MessageFormatter.arrayFormat(format, args).getMessage());
    }


    public void error(String format, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, MessageFormatter.arrayFormat(format, args).getMessage());
    }

    public void warn(String format, Object... args) {
        messager.printMessage(Diagnostic.Kind.WARNING, MessageFormatter.arrayFormat(format, args).getMessage());
    }

}
