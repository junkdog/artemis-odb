package com.artemis.generator.validator;

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

    public TypeModelValidator(Log log) {
        this.log = log;
    }

    /**
     * Scans for ambiguous methods.
     *
     * @param model
     * @throws TypeModelValidatorException if ambiguous methods.
     */
    public void validate(TypeModel model )
    {
        validateMethods(model.methods);
        validateFields(model.fields);
    }

    private void validateFields(List<FieldDescriptor> fields) {

    }

    private void validateMethods(List<MethodDescriptor> methods) {
        Collection<MethodDescriptor> duplicates =
                getDuplicateMethods(methods);

        String s = "";
        for (MethodDescriptor method : duplicates) {
            String error = " .. [" + method.getDebugNotes() + "] causes ambiguous method " + method.signature(true, true);
            log.error(error);
            s = s + error +"\n";
        }

        if (  duplicates.size() > 0 ) throw new TypeModelValidatorException("Ambiguous method(s).\n" + s);
    }


    public Collection<MethodDescriptor> getDuplicateMethods(List<MethodDescriptor> listContainingDuplicates)
    {
        final Map<String, MethodDescriptor> firstOccurances = new HashMap<String, MethodDescriptor>();
        final List<MethodDescriptor> duplicates = new ArrayList<MethodDescriptor>(128);
        final Set<String> uniques = new HashSet<String>(128);

        for (MethodDescriptor method : listContainingDuplicates)
        {
            // by ignoring parameter names and return types we pick up on more ambiguous cases.
            final String signature = method.signature(false, false);
            if (!uniques.add(signature))
            {
                MethodDescriptor firstOccuranceMethod = firstOccurances.remove(signature);
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
