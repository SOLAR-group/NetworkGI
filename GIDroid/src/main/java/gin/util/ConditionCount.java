package gin.util;

import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import gin.SourceFileTree;

import java.util.List;

public class ConditionCount {
    public static void count(SourceFileTree sf) {
        int count = 0;
        for (int i : sf.getStatementIDsInTargetMethod()){
            for (SourceFileTree.VariableTypeAndName v : sf.getPrimitiveVariablesInScopeForStatement(i)){
                if (v.getType().asPrimitiveType().getType() == PrimitiveType.Primitive.BOOLEAN){
                    count +=2;
                }
                else count += 25;
            }
            for (SourceFileTree.VariableTypeAndName v : sf.getNonePrimitiveVariablesInScopeForStatement(i)){
                Type type = v.getType();
                try {
                    ResolvedType resolvedType = type.resolve();
                    List<ResolvedMethodDeclaration> methods = resolvedType.asReferenceType().getAllMethods();
                    for (ResolvedMethodDeclaration m : methods){
                        if (m.getReturnType().isPrimitive() && m.getNumberOfParams() == 0){
                            if (m.getReturnType().asPrimitive().isBoolean()){
                                count +=2;
                            }
                            else count += 25;
                        }
                    }
                }
                catch (Exception e) {
                    continue;
                }


            }
        }
        System.out.println("if statement seacrh space" +count);
    }

}
