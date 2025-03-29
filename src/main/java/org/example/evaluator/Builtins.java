package org.example.evaluator;

import org.example.object.*;
import org.example.object.Object;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Builtins {
    public static final Map<String, Builtin> builtins = new HashMap<>();

    static {
        builtins.put("len", new Builtin(args -> {
            if (args.size() != 1) {
                return  new ErrorObject("wrong number of arguments. got="+args.size()+", want=1" );
            }

            Object arg = args.get(0);
            if (arg instanceof ArrayObject) {
                return new IntegerObject((long) ((ArrayObject) arg).getElements().size());
            } else if (arg instanceof StringObject) {
                return new IntegerObject(((StringObject) arg).getValue().length());
            }

            return  new ErrorObject("argument to `len` not supported, got "+ arg.type());
        }));
        builtins.put("first", new Builtin(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got="+args.size()+", want=1");
            }

            Object arg = args.get(0);
            if (!(arg instanceof ArrayObject)) {
                return new ErrorObject("argument to `first` must be ARRAY, got "+arg.type());
            }

            List<Object> elements = ((ArrayObject) arg).getElements();
            return elements.isEmpty() ? EvaluatorConstants.NULL : elements.get(0);
        }));

        builtins.put("last", new Builtin(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got="+args.size()+", want=1");
            }

            Object arg = args.get(0);
            if (!(arg instanceof ArrayObject)) {
                return new ErrorObject("argument to `last` must be ARRAY, got "+arg.type());
            }

            List<Object> elements = ((ArrayObject) arg).getElements();
            return elements.isEmpty() ? EvaluatorConstants.NULL : elements.get(elements.size() - 1);
        }));
        builtins.put("rest", new Builtin(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got="+ args.size()+", want=1");
            }

            Object arg = args.get(0);
            if (!(arg instanceof ArrayObject)) {
                return new ErrorObject("argument to `rest` must be ARRAY, got "+ arg.type());
            }

            List<Object> elements = ((ArrayObject) arg).getElements();
            if (elements.isEmpty()) {
                return EvaluatorConstants.NULL;
            }

            // Create new array with elements from index 1 onward
            List<Object> newElements = new ArrayList<>(
                    elements.subList(1, elements.size())
            );

            return new ArrayObject(newElements);
        }));
        builtins.put("push", new Builtin(args -> {
            if (args.size() != 2) {
                return new ErrorObject("wrong number of arguments. got="+args.size()+", want=2");
            }

            Object arg = args.get(0);
            if (!(arg instanceof ArrayObject)) {
                return new ErrorObject("argument to `push` must be ARRAY, got "+arg.type() );
            }

            ArrayObject array = (ArrayObject) arg;
            List<Object> newElements = new ArrayList<>(array.getElements());
            newElements.add(args.get(1));

            return new ArrayObject(newElements);
        }));
        builtins.put("print", new Builtin(args -> {
            for (Object arg : args) {
                if (arg instanceof org.example.object.Object) {
                    System.out.println((arg).inspect());
                } else {
                    System.out.println(arg.toString());
                }
            }
            return EvaluatorConstants.NULL;
        }));
        builtins.put("input", new Builtin(args -> {
            //System.out.print("input: ");
            String input = new java.util.Scanner(System.in).nextLine();
            return new StringObject(input);
        }));
        builtins.put("int", new Builtin(args -> {
            if (args.size() != 1) {
                return new ErrorObject("wrong number of arguments. got="+args.size()+", want=1");
            }

            Object arg = args.get(0);
            if (arg instanceof IntegerObject) {
                return arg;
            }

            if (arg instanceof StringObject) {
                String str = ((StringObject) arg).getValue();
                try {
                    return new IntegerObject(Long.parseLong(str));
                } catch (NumberFormatException e) {
                    return new ErrorObject("could not convert to int: "+str);
                }
            }

            return new ErrorObject("argument to `int` not supported, got "+arg.type());
        }) );

        builtins.put("range", new Builtin(args -> {

            if (args.size() < 1 || args.size() > 3) {
                return new ErrorObject("wrong number of arguments. got=" + args.size() + ", want=1,2,3");
            }

            long from = args.size() > 1 ? ((IntegerObject) args.get(0)).getValue() : 0;
            long to = ((IntegerObject) args.get(args.size() == 1 ? 0 : 1)).getValue();
            long increment = args.size() == 3 ? ((IntegerObject) args.get(2)).getValue() : 1;

            List<Object> newElements = new ArrayList<>();

            for (long i = from; i < to; i+=increment) {
                    newElements.add(new IntegerObject(i));
            }
            return new ArrayObject(newElements);
        }));
    }

    public static Builtin get(String name) {
        return builtins.get(name);
    }

    public static boolean exists(String name) {
        return builtins.containsKey(name);
    }
}