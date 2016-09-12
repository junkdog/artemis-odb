package com.artemis.generator.validator;

import com.artemis.generator.model.type.AmbiguousSignature;
import com.artemis.generator.model.type.FieldDescriptor;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.Log;

import java.util.*;

/**
 * @author Daan van Yperen
 */
public class TypeModelValidator {

    private Log log;
    private String context;

    public TypeModelValidator(Log log, String context) {
        this.log = log;
        this.context = context;
    }

    /**
     * Scans for ambiguous methods.
     *
     * @param model
     * @throws TypeModelValidatorException if ambiguous methods.
     */
    public void validate(TypeModel model )
    {
        String errors="";
        errors += validateFields(model.fields);
        errors += validateMethods(model.methods);

        if ( !errors.isEmpty() )
        {
            throw new TypeModelValidatorException("Ambiguous field(s) or method(s).\n" + errors);
        }
    }

    private String validateFields(List<FieldDescriptor> fields) {
        Collection<FieldDescriptor> duplicates =
                getDuplicates(fields);

        String s = "";
        for (FieldDescriptor field : duplicates) {
            String error = " .. [" + field.getDebugNotes() + "] causes ambiguous field " + field.name + " in " + context;
            log.error(error);
            s = s + error +"\n";
        }

        return s;
    }

    private String validateMethods(List<MethodDescriptor> methods) {
        Collection<MethodDescriptor> duplicates =
                getDuplicates(methods);

        String s = "";
        for (MethodDescriptor method : duplicates) {
            String error = " .. [" + method.getDebugNotes() + "] causes ambiguous method " + method.signature(true, true)+ " in " + context;
            log.error(error);
            s = s + error +"\n";
        }

        return s;
    }


    public <T extends AmbiguousSignature> Collection<T> getDuplicates(List<T> listContainingDuplicates)
    {
        final Map<String, T> firstOccurances = new HashMap<String, T>();
        final List<T> duplicates = new ArrayList<T>(128);
        final Set<String> uniques = new HashSet<String>(128);

        for (T method : listContainingDuplicates)
        {
            // by ignoring parameter names and return types we pick up on more ambiguous cases.
            final String signature = method.ambiguousSignature();
            if (!uniques.add(signature))
            {
                T firstOccuranceMethod = firstOccurances.remove(signature);
                if ( firstOccuranceMethod != null )
                {
                    duplicates.add(firstOccuranceMethod);
                }
                duplicates.add(method);
            } else {
                firstOccurances.put(signature, method);
            }
        }
        return duplicates;
    }
}
