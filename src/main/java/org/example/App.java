package org.example;

import org.example.repl.Repl;

import java.io.PrintWriter;

/**
 * Hello world!let x 12 * 3
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Repl repl = new Repl();
        repl.start(System.in, new PrintWriter(System.out, true));
    }
}
