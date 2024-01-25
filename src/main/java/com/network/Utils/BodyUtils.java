package com.network.Utils;

import soot.Body;
import soot.Local;
import soot.RefType;
import soot.jimple.Jimple;

public class BodyUtils {

    public static Local addLocal(Body body, String name, String className)
    {
        Local tmpRef = Jimple.v().newLocal(name, RefType.v(className));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }

    public static Local addTag(Body body)
    {
        Local tmpString = Jimple.v().newLocal("tag", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }
}
