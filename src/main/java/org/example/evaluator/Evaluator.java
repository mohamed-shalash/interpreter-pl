package org.example.evaluator;

import org.example.ast.*;
import org.example.ast.classes.*;
import org.example.object.*;
import org.example.object.Object;
import org.example.object.classes.ClassObject;
import org.example.object.classes.InstanceObject;
import org.example.object.classes.MethodObject;

import java.util.*;

public class Evaluator {

    public static Object eval(Node node, Environment env) {
        /*Builtins.builtins.forEach((name, builtin) -> {
            env.set(name, builtin);
        });*/
        if (node instanceof Program) {
            return evalProgram((Program) node,env);
        }
        else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).getExpression(),env);
        }
        else if (node instanceof IntegerLiteral) {
            return new IntegerObject(((IntegerLiteral) node).getValue());
        }
        else if (node instanceof BooleanLiteral) {
            return nativeBoolToBooleanObject(((BooleanLiteral) node).getValue());
        }
        else if (node instanceof Identifier) {
            /*Identifier ident = (Identifier) node;
            Object value = env.get(ident.getValue());
            //System.out.println("Resolving " + ident.getValue() + " to " + value);
            if (value == null) {
                return newError("identifier not found: " + ident.getValue());
            }
            return value;*/
            return evalIdentifier((Identifier) node,env);
        }
        else if (node instanceof ContinueStatement) {
            return ContinueObject.CONTINUE;
        }
        else if (node instanceof BreakStatement) {
            return BreakObject.BREAK;
        }
        else if (node instanceof PrefixExpression) {
            PrefixExpression prefix = (PrefixExpression) node;
            Object right = eval(prefix.getRight(),env);
            if (isError(right)) {
                return right;
            }
            return evalPrefixExpression(prefix.getOperator(), right);
        }
        else if (node instanceof LogicalExpression) {
            LogicalExpression logOp = (LogicalExpression) node;
            return evalLogicalOperation(logOp, env);
        }
        else if (node instanceof InfixExpression) {
            InfixExpression infix = (InfixExpression) node;
            Object left = eval(infix.getLeft(),env);
            if (isError(left)) {
                return left;
            }
            Object right = eval(infix.getRight(),env);
            if (isError(right)) {
                return right;
            }
            return evalInfixExpression(infix.getOperator(), left, right);
        }
        else if (node instanceof BlockStatement) {
            return evalBlockStatement((BlockStatement) node,env);
        }
        else if (node instanceof IfExpression) {
            IfExpression ifExp = (IfExpression) node;
            Object condition = eval(ifExp.getCondition(),env);
            if (isError(condition)) {
                return condition;
            }
            if (isTruthy(condition)) {
                return eval(ifExp.getConsequence(),env);
            } else if (ifExp.getAlternative() != null) {
                return eval(ifExp.getAlternative(),env);
            } else {
                return EvaluatorConstants.NULL;
            }
        }
        else if (node instanceof IfElseIfExpression) {
            IfElseIfExpression ifExp = (IfElseIfExpression) node;
            Object condition = eval(ifExp.getCondition(),env);
            if (isError(condition)) {
                return condition;
            }
            if (isTruthy(condition)) {
                return eval(ifExp.getConsequence(),env);
            } else if (ifExp.getAlternative() != null) {
                return eval(ifExp.getAlternative(),env);
            } else {
                return EvaluatorConstants.NULL;
            }
        }
        else if (node instanceof WhileExpression) {
            return evalWhileLoop((WhileExpression) node, env);
        }
        else if (node instanceof ForExpression) {
            return evalForLoop((ForExpression) node, env);
        }
        else if (node instanceof ReturnStatement) {
            ReturnStatement rs = (ReturnStatement) node;
            Object val = eval(rs.getReturnValue(),env);
            if (isError(val)) {
                return val;
            }
            return new ReturnValue(val);
        }
        else if (node instanceof LetStatement) {
            LetStatement let = (LetStatement) node;
            Object value = eval(let.getValue(), env);

            if (isError(value)) {
                return value;
            }

            // Store the value in the environment
            env.set(let.getName().getValue(), value);
            //System.out.println("Set " + let.getName().getValue() + " to " + value);
            return value;
        }

        else if (node instanceof FunctionLiteral) {
            FunctionLiteral fn = (FunctionLiteral) node;
            return new FunctionObject(fn.getParameters(), fn.getBody(), env);
        }
        else if (node instanceof CallExpression){
            CallExpression call = (CallExpression) node;
            Object fn = eval(call.getFunction(),env);
            if (isError(fn)) {
                return fn;
            }
            List<Object> args = evalExpressions(call.getArguments(), env);
            if (args.size() == 1 && isError(args.get(0))) {
                return args.get(0);
            }
            return applyFunction(fn, args);
        }
        else if (node instanceof AssignmentExpression) {
            AssignmentExpression assign = (AssignmentExpression) node;
            Object value = eval(assign.getValue(), env);
            if (isError(value)) {
                return value;
            }
            env.set(assign.getName().getValue(), value);
            return value;
        }
        else if (node instanceof PropertyAssignmentExpression) {
            return evalPropertyAssignment((PropertyAssignmentExpression) node, env);
        }
        else if (node instanceof StringLiteral) {
            StringLiteral strLit = (StringLiteral) node;
            return new StringObject(strLit.getValue());
        }
        else if (node instanceof ArrayLiteral) {
            ArrayLiteral arrayLiteral = (ArrayLiteral) node;
            List<Object> elements = evalExpressions(arrayLiteral.getElements(), env);

            if (elements.size() == 1 && isError(elements.get(0))) {
                return elements.get(0);
            }
            return new ArrayObject(elements);
        }
        else if (node instanceof IndexExpression) {
            IndexExpression idxExp = (IndexExpression) node;
            return evalIndexExpression(idxExp, env);
        }
        else  if (node instanceof HashLiteral) {
            return evalHashLiteral((HashLiteral) node, env);
        }
        else if (node instanceof ThisExpression) {
            Object thisObj = env.get("this");
            if (thisObj == null) {
                return newError("'this' is not defined in this context");
            }
            return thisObj;
        }
        else if (node instanceof ClassDeclaration) {
            return evalClassDeclaration((ClassDeclaration) node, env);
        }
        else if (node instanceof NewInstanceExpression){
            return evalNewInstance((NewInstanceExpression)node,env);
        }
        else if (node instanceof MemberExpression) {
            MemberExpression member = (MemberExpression) node;
            Object object = eval(member.getObject(), env);

            if (isError(object)) return object;
            if (!(object instanceof InstanceObject)) {
                return newError("member access on non-instance: " + type(object));
            }

            InstanceObject instance = (InstanceObject) object;
            String propName = member.getProperty().getValue();

            // 1. Check methods
            MethodObject method = instance.getClassObject().getMethod(propName);
            if (method != null) return method.bind(instance);

            // 2. Check fields with access control
            FieldDeclaration field = instance.getClassObject().getField(propName);
            if (field != null) {
                // Check private access
                if (field.isPrivate()) {
                    Object thisObj = env.get("this");
                    if (!(thisObj instanceof InstanceObject) ||
                            ((InstanceObject) thisObj).getClassObject() != instance.getClassObject()) {
                        return newError("Private field '"+propName+"' accessed outside class");
                    }
                }
                Object propertyValue = instance.getProperty(propName);
                return (propertyValue != null) ? propertyValue : newError("Undefined property: " + propName);
            }
            return newError("Undefined property/method: " + propName);
        }
        return EvaluatorConstants.NULL;
    }


    private static boolean isError(Object obj) {
        return obj instanceof ErrorObject;
    }


    private static Object evalLogicalOperation(LogicalExpression logOp, Environment env) {
        Object left = eval(logOp.getLeft(), env);
        if (isError(left)) return left;

        String operator = logOp.getOperator();
        boolean leftTruthy = isTruthy(left);

        // Short-circuit evaluation
        if (operator.equals("and") || operator.equals("&&")) {
            if (!leftTruthy) {
                return EvaluatorConstants.FALSE;
            }
            Object right = eval(logOp.getRight(), env);
            return nativeBoolToBooleanObject(isTruthy(right));
        } else if (operator.equals("or") || operator.equals("||")) {
            if (leftTruthy) {
                return EvaluatorConstants.TRUE;
            }
            Object right = eval(logOp.getRight(), env);
            return nativeBoolToBooleanObject(isTruthy(right));
        } else {
            return newError("unknown logical operator: " + operator);
        }
    }

    private static Object evalInfixExpression(String operator, Object left, Object right) {
        // Handle equality/inequality across types first
        switch (operator) {
            case "==":
                return nativeBoolToBooleanObject(areEqual(left, right));
            case "!=":
                return nativeBoolToBooleanObject(!areEqual(left, right));
            }

        // Then check for type mismatch for other operators (x == 10) && (3 > 1)
        if (!left.type().equals(right.type())) {
            return newError("type mismatch: " + left.type() + " " + operator + " " + right.type());
        }

        if (left instanceof StringObject && right instanceof StringObject) {
            return evalStringInfixExpression(operator,
                    (StringObject) left,
                    (StringObject) right);
        }

        if (left instanceof IntegerObject && right instanceof IntegerObject) {
            return evalIntegerInfixExpression(operator,
                    ((IntegerObject) left).getValue(),
                    ((IntegerObject) right).getValue());
        }

        if (left instanceof BooleanObject && right instanceof BooleanObject) {
            return evalBooleanInfixExpression(operator,
                    ((BooleanObject) left).getValue(),
                    ((BooleanObject) right).getValue());
        }

        return newError("unknown operator: " + left.type() + " " + operator + " " + right.type());
    }

    private static Object evalStringInfixExpression(String operator,
                                                    StringObject left,
                                                    StringObject right) {
        String leftVal = left.getValue();
        String rightVal = right.getValue();

        return switch (operator) {
            case "+" -> new StringObject(leftVal + rightVal);
            default -> new ErrorObject(
                    "unknown operator: STRING " + operator + " STRING"
            );
        };
    }
    private static Object evalIntegerInfixExpression(String operator, long left, long right) {
        return switch (operator) {
            case "+" -> new IntegerObject(left + right);
            case "-" -> new IntegerObject(left - right);
            case "*" -> new IntegerObject(left * right);
            case "/" -> new IntegerObject(left / right);
            case "<" -> nativeBoolToBooleanObject(left < right);
            case ">" -> nativeBoolToBooleanObject(left > right);
            case "<=" -> nativeBoolToBooleanObject(left <= right);
            case ">=" -> nativeBoolToBooleanObject(left >= right);
            case "==" -> nativeBoolToBooleanObject(left == right);
            case "!=" -> nativeBoolToBooleanObject(left != right);
            default ->  newError("unknown operator: INTEGER "+operator+" INTEGER");
        };
    }

    private static boolean isTruthy(Object obj) {
        if (obj == EvaluatorConstants.NULL) return false;
        if (obj == EvaluatorConstants.FALSE) return false;
        if (obj == EvaluatorConstants.TRUE) return true;
        if (obj instanceof IntegerObject) {
            return ((IntegerObject) obj).getValue() != 0;
        }
        return true;
    }


    private static Object evalBooleanInfixExpression(String operator,
                                                     boolean left,
                                                     boolean right) {
        return switch (operator) {
            case "==" -> nativeBoolToBooleanObject(left == right);
            case "!=" -> nativeBoolToBooleanObject(left != right);
            default -> newError("unknown operator: BOOLEAN " + operator + " BOOLEAN");
        };
    }
    private static boolean areEqual(Object a, Object b) {
        // Null checks
        if (a == EvaluatorConstants.NULL && b == EvaluatorConstants.NULL) return true;
        if (a == EvaluatorConstants.NULL || b == EvaluatorConstants.NULL) return false;

        // Integer comparisons
        if (a instanceof IntegerObject && b instanceof IntegerObject) {
            return ((IntegerObject) a).getValue() == ((IntegerObject) b).getValue();
        }

        // Boolean comparisons
        if (a instanceof BooleanObject && b instanceof BooleanObject) {
            return ((BooleanObject) a).getValue() == ((BooleanObject) b).getValue();
        }

        // Default reference equality
        return a.equals(b);
    }


    private static Object evalPrefixExpression(String operator, Object right) {
        switch (operator) {
            case "!":
                return evalBangOperator(right);
            case "-":
                return evalMinusPrefixOperator(right);
            default:
                return newError("unknown operator: "+ operator +" "+ right.type());
        }
    }
    public static List<Object> evalExpressions(List<Expression> expressions, Environment env) {
        List<Object> results = new ArrayList<>();

        for (Expression exp : expressions) {
            Object evaluated = eval(exp, env);

            if (evaluated instanceof ErrorObject) {
                return Collections.singletonList(evaluated);
            }

            results.add(evaluated);
        }

        return results;
    }


    private static Object evalMinusPrefixOperator(Object right) {

        /*if (right.type() != ObjectType.INTEGER) {
            return newError("unknown operator:-%s", right.type());
        }*/
        if (!(right instanceof IntegerObject)) {
            return newError("unknown operator: -"+ right.type());
        }

        long value = ((IntegerObject) right).getValue();
        return new IntegerObject(-value);
    }




    private static Object evalHashIndexExpression(HashObject hash, Object index) {
        if (!(index instanceof Hashable)) {
            return newError("unusable as hash key: " + type(index));
        }

        HashKey key = ((Hashable) index).hashKey();
        HashPair pair = hash.getPairs().get(key);

        return (pair != null) ? pair.getValue() : EvaluatorConstants.NULL;
    }


    private static Object evalIndexExpression(IndexExpression exp, Environment env) {
        Object left = eval(exp.getLeft(), env);
        if (isError(left)) return left;

        Object index = eval(exp.getIndex(), env);
        if (isError(index)) return index;

        return evalIndex(left, index);
    }

    private static Object evalHashLiteral(HashLiteral node, Environment env) {
        Map<HashKey, HashPair> pairs = new HashMap<>();

        for (Map.Entry<Expression, Expression> entry : node.getPairs().entrySet()) {
            Expression keyNode = entry.getKey();
            Expression valueNode = entry.getValue();

            // Evaluate key
            Object key = eval(keyNode, env);
            if (key instanceof ErrorObject) {
                return key;
            }

            // Check if key is hashable
            if (!(key instanceof Hashable)) {
                return new ErrorObject("unusable as hash key: " + ((Object) key).type());
            }

            // Evaluate value
            Object value = eval(valueNode, env);
            if (value instanceof ErrorObject) {
                return value;
            }

            // Store pair
            HashKey hashedKey = ((Hashable) key).hashKey();
            pairs.put(hashedKey, new HashPair(key, value));
        }

        return new HashObject(pairs);
    }

    private static Object evalIndex(Object left, Object index) {
        if (left instanceof ArrayObject && index instanceof IntegerObject) {
            return evalArrayIndex((ArrayObject) left, (IntegerObject) index);
        }else if (left instanceof HashObject) {
            return evalHashIndexExpression((HashObject) left, index);
        }
        return newError("index operator not supported for types: "+type(left)+"["+type(index)+"]");
    }


    private static Object evalArrayIndex(ArrayObject array, IntegerObject index) {
        long idx = index.getValue();
        List<Object> elements = array.getElements();

        if (idx < 0 || idx >= elements.size()) {
            return EvaluatorConstants.NULL;
        }
        return elements.get((int) idx);
    }

    private static String type(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Object) return ((Object) obj).type().toString();
        return obj.getClass().getSimpleName();
    }




    private static Object evalBangOperator(Object right) {
        if (right == EvaluatorConstants.TRUE) {
            return EvaluatorConstants.FALSE;
        } else if (right == EvaluatorConstants.FALSE) {
            return EvaluatorConstants.TRUE;
        } else if (right == EvaluatorConstants.NULL) {
            return EvaluatorConstants.TRUE;
        }
        return EvaluatorConstants.FALSE;
    }

    public static Object evalProgram(Program program, Environment env) {
        Object result = EvaluatorConstants.NULL;

        for (Statement statement : program.getStatements()) {
            result = eval(statement,env);

            // Handle ReturnValue and Error propagation
            if (result instanceof ReturnValue) {
                // Unwrap return value for program-level returns
                return ((ReturnValue) result).getValue();
            } else if (result instanceof ErrorObject) {
                // Propagate errors immediately
                return result;
            }
        }

        return result;
    }
    private static Object evalBlockStatement(BlockStatement block, Environment env) {
        Object result = EvaluatorConstants.NULL;

        for (Statement statement : block.getStatements()) {
            result = eval(statement, env);

            if (result != null) {
                ObjectType type = result.type();
                if (type == ObjectType.RETURN_VALUE || type == ObjectType.ERROR ||
                        type == ObjectType.CONTINUE || type == ObjectType.BREAK) {
                    return result; // Propagate CONTINUE and BREAK immediately
                }
            }
        }

        return result;
    }
    private static Object applyFunction3(Object fn, List<Object> args) {
        if (!(fn instanceof FunctionObject)) {
            return newError("not a function: " + objectType(fn));
        }

        FunctionObject function = (FunctionObject) fn;

        // Validate parameter count
        if (args.size() != function.getParameters().size()) {
            return newError("argument count mismatch: expected="+function.getParameters().size()+", got="+args.size());
        }

        Environment extendedEnv = extendFunctionEnv(function, args);
        Object result = eval(function.getBody(), extendedEnv);
        return unwrapReturnValue(result);
    }

    private static Object applyFunction(Object fn, List<Object> args) {
        if (fn instanceof MethodObject) {
            MethodObject method = (MethodObject) fn;
            FunctionObject function = method.getFunction();
            Environment extendedEnv = extendFunctionEnv(function, args);
            extendedEnv.set("this", method.getInstance());
            return eval(function.getBody(), extendedEnv);
        } else if (fn instanceof FunctionObject) {
            FunctionObject function = (FunctionObject) fn;
            Environment extendedEnv = extendFunctionEnv(function, args);
            Object result = eval(function.getBody(), extendedEnv);
            return unwrapReturnValue(result);
        } else if (fn instanceof Builtin) {
            return ((Builtin) fn).apply(args);
        }
        return newError("not a function: " + type(fn));
    }
    /*private static Environment extendFunctionEnv(FunctionObject function,
                                                 List<Object> args) {
        Environment env = Environment.newEnclosed(function.getEnv());

        List<Identifier> params = function.getParameters();
        for (int i = 0; i < params.size(); i++) {
            Identifier param = params.get(i);
            env.set(param.getValue(), args.get(i));
        }

        return env;
    }*/

    private static Environment extendFunctionEnv(FunctionObject fn, List<Object> args) {
        Environment env = Environment.newEnclosed(fn.getEnv());
        for (int i = 0; i < fn.getParameters().size(); i++) {
            env.set(fn.getParameters().get(i).getValue(), args.get(i));
        }
        return env;
    }

    private static Object unwrapReturnValue(Object obj) {
        return (obj instanceof ReturnValue)
                ? ((ReturnValue) obj).getValue()
                : obj;
    }

    private static String objectType(Object obj) {
        return (obj instanceof Object)
                ? ((Object) obj).type().name()
                : "UNKNOWN";
    }

    private static BooleanObject nativeBoolToBooleanObject(boolean input) {
        return input ? EvaluatorConstants.TRUE : EvaluatorConstants.FALSE;
    }

    private static ErrorObject newError(String format) {
        return new ErrorObject(format);
    }

    private static Object evalPropertyAssignment(PropertyAssignmentExpression node, Environment env) {
        // Evaluate the object (e.g., 'this' in 'this.name')
        Object object = eval(node.getObject(), env);

        if (object instanceof ErrorObject) return object;

        // Verify we're assigning to an instance
        if (!(object instanceof InstanceObject)) {
            return newError("Left side of . assignment must be an instance");
        }

        // Evaluate the value to assign
        Object value = eval(node.getValue(), env);
        if (value instanceof ErrorObject) return value;

        // Set the property on the instance
        String propName = node.getProperty().getValue();
        ((InstanceObject) object).setProperty(propName, value);

        return value;
    }

    private static Object evalNewInstance(NewInstanceExpression node, Environment env) {
        Object classObj = env.get(node.getClassName().getValue());
        if (!(classObj instanceof ClassObject)) {
            return newError("undefined class: " + node.getClassName().getValue());
        }

        InstanceObject instance = new InstanceObject((ClassObject) classObj);
        ClassObject currentClass = (ClassObject) classObj;

        // Initialize fields with their default values
        while (currentClass != null) {
            for (FieldDeclaration field : currentClass.getFields().values()) {
                Object value = EvaluatorConstants.NULL;
                if (field.getInitializer() != null) {
                    value = eval(field.getInitializer(), env);
                    if (isError(value)) return value;
                }
                instance.setProperty(field.getName().getValue(), value);
            }
            currentClass = currentClass.getSuperClass();
        }

        // Handle constructor
        MethodObject constructor = ((ClassObject) classObj).getMethod("init");
        if (constructor != null) {
            List<Object> args = evalExpressions(node.getArguments(), env);
            if (args.size() == 1 && isError(args.get(0))) {
                return args.get(0);
            }
            applyFunction(constructor.bind(instance), args);
        }

        return instance;
    }

    private static Object evalClassDeclaration(ClassDeclaration node, Environment env) {
        Map<String, FieldDeclaration> fields = new HashMap<>();
        Map<String, MethodObject> methods = new HashMap<>();

        // Process members
        for (ClassMember member : node.getMembers()) {
            if (member instanceof FieldDeclaration) {
                fields.put(((FieldDeclaration) member).getName().getValue(), (FieldDeclaration) member);
            } else if (member instanceof MethodDeclaration) {
                MethodDeclaration methodDecl = (MethodDeclaration) member;
                methods.put(methodDecl.getName().getValue(),
                        new MethodObject(
                                methodDecl.getParameters(),
                                methodDecl.getBody(),
                                env
                        )
                );
            }
        }

        ClassObject classObj = new ClassObject(
                node.getName().getValue(),
                (node.getSuperClass() != null) ? resolveSuperClass(node.getSuperClass(), env) : null,
                fields,
                methods
        );
        env.set(node.getName().getValue(), classObj);
        return classObj;
    }

    private static ClassObject resolveSuperClass(Identifier superClass, Environment env) {
        Object superClassObj = env.get(superClass.getValue());

        if (superClassObj == null) {
            return null; // Or throw error: "Superclass not found"
        }

        if (!(superClassObj instanceof ClassObject)) {
            return null; // Or throw error: "Superclass is not a class"
        }

        return (ClassObject) superClassObj;
    }

 /*   private static Object evalMemberExpression(MemberExpression member, Environment env) {
        Object object = eval(member.getObject(), env);
        if (!(object instanceof InstanceObject)) {
            return newError("Member access on non-instance");
        }

        InstanceObject instance = (InstanceObject) object;
        String propertyName = member.getProperty().getValue();

        // Check for methods first
        MethodObject method = instance.getClassObject().getMethod(propertyName);
        if (method != null) {
            return method.bind(instance);
        }

        // Check for fields
        FieldDeclaration field = instance.getClassObject().getField(propertyName);
        if (field != null) {
            if (field.isPrivate()) {
                // Check if access is within the class
                Object thisObj = env.get("this");
                if (thisObj instanceof InstanceObject) {
                    InstanceObject thisInstance = (InstanceObject) thisObj;
                    if (thisInstance.getClassObject() != instance.getClassObject()) {
                        return newError("Cannot access private field from another class");
                    }
                } else {
                    return newError("Private field accessed outside of class method");
                }
            }
            return instance.getProperty(propertyName);
        }

        return newError("Undefined property or method: " + propertyName);
    }
*/
  /*  private static boolean isInsideClassMethod(Environment env) {
        // Assume that inside a method, the environment has a "this" binding
        return env.get("this") != null;
    }*/
    private static Object evalIdentifier(Identifier node, Environment env) {
        Object value = env.get(node.getValue());
        if (value != null) {
            return value;
        }

        if (Builtins.exists(node.getValue())) {
            return Builtins.get(node.getValue());
        }

        return new ErrorObject("identifier not found: " + node.getValue());

    }

    private static Object evalWhileLoop(WhileExpression node, Environment env) {
        Object result = EvaluatorConstants.NULL;

        while (true) {
            Object condition = eval(node.getCondition(), env);
            if (isError(condition)) {
                return condition;
            }

            if (!isTruthy(condition)) {
                break;
            }

            // Evaluate body statements individually to control side effects
            BlockStatement body = node.getBody();
            for (Statement stmt : body.getStatements()) {
                result = eval(stmt, env);

                if (result instanceof BreakObject) {
                    return EvaluatorConstants.NULL;
                } else if (result instanceof ContinueObject) {
                    break; // Break out of body evaluation, continue loop
                } else if (result instanceof ReturnValue) {
                    return ((ReturnValue) result).getValue();
                } else if (result instanceof ErrorObject) {
                    return result;
                }

                condition = eval(node.getCondition(), env);
                if (!isTruthy(condition)) {
                    return EvaluatorConstants.NULL;
                }
            }
        }

        return EvaluatorConstants.NULL;
    }/* while (i < 5) { print(i); let i = i + 1; }        let i = 0; while(i<=9){i=i+1; if(i==5){continue;} else {print(i);}};  while(i<9){i=i+1; if(i==5){print("exceeded");break;} else {print(i);}};*/


    private static Object evalForLoop(ForExpression node, Environment env) {
        Expression iterable = node.getIterable();
        BlockStatement body = node.getBody();

        Object iterableObj = eval(iterable, env);
        if (isError(iterableObj)) {
            return iterableObj;
        }

        if (!(iterableObj instanceof ArrayObject)) {
            return newError("not iterable: " + iterableObj.type());
        }

        ArrayObject array = (ArrayObject) iterableObj;
        List<Object> elements = array.getElements();
        Environment loopEnv = Environment.newEnclosed(env);
        for (Object element : elements) {
            loopEnv.set(node.getLoopVariable().getValue(), element);

            Object result = eval(body, loopEnv);
            if (result instanceof BreakObject) {
                return EvaluatorConstants.NULL;
            } else if (result instanceof ContinueObject) {
                continue; // Break out of body evaluation, continue loop
            } else if (result instanceof ReturnValue) {
                return ((ReturnValue) result).getValue();
            } else if (result instanceof ErrorObject) {
                return result;
            }
        }
        return EvaluatorConstants.NULL;

    }//for i in range(1,50,5){if(i==16){print("exceeded");break;} else {print(i);}};
}
