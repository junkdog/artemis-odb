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
			
			FieldInsnNode f = (FieldInsnNode)node;
			if (className.equals(f.owner))
				continue;
			
			// TODO: doesn't handle manual setters/getters
			if (PUTFIELD == f.getOpcode() && components.containsKey(f.owner)) {
				changed = true;
				i = InstructionMutator.on(instructions, f)
					.insertAtOffset(0,
						new MethodInsnNode(INVOKEVIRTUAL, f.owner, f.name, param(f)))
					.delete(0)
					.transform();
			} else if (GETFIELD == f.getOpcode() && components.containsKey(f.owner)) {
				changed = true;
				
				i = on(instructions, f)
					.insertAtOffset(0,
						new MethodInsnNode(INVOKEVIRTUAL, f.owner, f.name, "()" + f.desc))
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
