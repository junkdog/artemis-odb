package com.artemis.weaver.pooled;

import static com.artemis.meta.ClassMetadataUtil.instanceFields;
import static com.artemis.meta.ClassMetadataUtil.sizeOf;

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
			if (isZeroable(field)) {
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

	private boolean isZeroable(FieldDescriptor f) {
		 // primitive fields reports size > 0
		return sizeOf(f) > 0 || "Ljava/lang/String;".equals(f.desc);
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
		mv.visitInsn(constInstructionFor(field));
		mv.visitFieldInsn(PUTFIELD, meta.type.getInternalName(), field.name, field.desc);
	}

	private static int constInstructionFor(FieldDescriptor field) {
		if ("Z".equals(field.desc))
			return ICONST_0;
		if ("C".equals(field.desc))
			return ICONST_0;
		if ("S".equals(field.desc))
			return ICONST_0;
		if ("I".equals(field.desc))
			return ICONST_0;
		if ("J".equals(field.desc))
			return LCONST_0;
		if ("F".equals(field.desc))
			return FCONST_0;
		if ("D".equals(field.desc))
			return DCONST_0;
		if ("Ljava/lang/String;".equals(field.desc))
			return ACONST_NULL;

		throw new RuntimeException(field.toString());
	}
}
