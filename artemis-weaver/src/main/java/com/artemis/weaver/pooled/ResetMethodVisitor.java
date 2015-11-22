package com.artemis.weaver.pooled;

import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.FieldDescriptor;
import org.objectweb.asm.Type;

import java.util.regex.Pattern;

public class ResetMethodVisitor extends MethodVisitor implements Opcodes {

	private final ClassMetadata meta;

	private static final Pattern intBags =
		Pattern.compile("Lcom\\/artemis\\/utils\\/.*Bag;");
	private static final Pattern mapSetsListsInterfaces =
		Pattern.compile("Ljava\\/util\\/(List|Map|Set);");
	private static final Pattern mapSetsLists =
		Pattern.compile("Ljava\\/util\\/.+(List|Map|Set);");
	private static final Pattern libgdxCollections =
		Pattern.compile("Lcom\\/badlogic\\/gdx\\/utils\\/.*(Array|Map);");



	public ResetMethodVisitor(MethodVisitor mv, ClassMetadata meta) {
		super(ASM4, mv);
		this.meta = meta;
	}

	@Override
	public void visitCode() {
		mv.visitCode();
		for (FieldDescriptor field : instanceFields(meta)) {
			if (field.isResettable()) {
				resetField(field);
			} else if (mapSetsListsInterfaces.matcher(field.desc).find()) {
				clearCollection(field, INVOKEINTERFACE);
			} else if (isClearable(field)) {
				clearCollection(field, INVOKEVIRTUAL);
			}
		}
	}

	private boolean isClearable(FieldDescriptor f) {
		return mapSetsLists.matcher(f.desc).find()
			|| intBags.matcher(f.desc).find()
			|| libgdxCollections.matcher(f.desc).find();
	}

	private void clearCollection(FieldDescriptor field, int invoke) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, meta.type.getInternalName(), field.name, field.desc);
		mv.visitMethodInsn(invoke,
			Type.getType(field.desc).getInternalName(),
			"clear", "()V", (invoke == INVOKEINTERFACE));
	}

	private void resetField(FieldDescriptor field) {
		mv.visitVarInsn(ALOAD, 0);
		field.reset.accept(mv);
		mv.visitFieldInsn(PUTFIELD, meta.type.getInternalName(), field.name, field.desc);
	}
}
