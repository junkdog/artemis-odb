package com.artemis.weaver.packed;

import static com.artemis.weaver.packed.InstructionMutator.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.transformer.MethodTransformer;

class ExternalFieldMethodTransformer extends MethodTransformer implements Opcodes {

	private final Map<String, ClassMetadata> components;
	private String className; 

	public ExternalFieldMethodTransformer(MethodTransformer mt, String className, List<ClassMetadata> packedComponents) {
		super(mt);
		this.className = className;
		
		components = new HashMap<String, ClassMetadata>();
		for (ClassMetadata meta : packedComponents) {
			components.put(meta.type.getInternalName(), meta);
		}
	}

	@Override
	public boolean transform(MethodNode mn) {
		InsnList instructions = mn.instructions;
		boolean changed = false;
		
		for (int i = 0; instructions.size() > i; i++) {
			AbstractInsnNode node = instructions.get(i);
			if (AbstractInsnNode.FIELD_INSN != node.getType())
				continue;
			
			FieldInsnNode fn = (FieldInsnNode)node;
			if (className.equals(fn.owner))
				continue;
			
			if (PUTFIELD == fn.getOpcode() && components.containsKey(fn.owner)) {
				changed = true;
				i = InstructionMutator.on(instructions, fn)
					.insertAtOffset(0,
						new MethodInsnNode(INVOKEVIRTUAL, fn.owner, fn.name, param(fn)))
					.delete(0)
					.transform();
			} else if (GETFIELD == fn.getOpcode() && components.containsKey(fn.owner)) {
				changed = true;
				
				i = on(instructions, fn)
					.insertAtOffset(0,
						new MethodInsnNode(INVOKEVIRTUAL, fn.owner, fn.name, "()" + fn.desc))
					.delete(0)
					.transform();
			}
		}
		
		
		return changed || super.transform(mn);
	}

	private static String param(FieldInsnNode n) {
		return "(" + n.desc + ")V";
	}
	
}
