package com.google.jbuenosv.blog.ingestga.cloudfunctions.application.command;

/**
 * Created by jbuenosv@google.com
 */
public interface CommandHandler {
    /**
     * Executes the command
     */
    void execute(Command command) ;
}
