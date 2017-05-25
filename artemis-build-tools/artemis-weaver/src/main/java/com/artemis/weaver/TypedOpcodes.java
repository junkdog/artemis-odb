package com.artemis.weaver;

import static com.artemis.meta.ClassMetadataUtil.instanceFieldTypes;

import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.FieldDescriptor;

public final class TypedOpcodes implements Opcodes {

	private final char desc;

	public TypedOpcodes(ClassMetadata meta) {
		String type = instanceFieldTypes(meta).iterator().next();
		assert(type.length() <= 1);
		
		this.desc = (type.length() == 1)
			? type.charAt(0)
			: 'X';
	}
	
	public TypedOpcodes(FieldDescriptor f) {
		assert(f.desc.length() == 1);
		
		this.desc = (f.desc.length() == 1)
			? f.desc.charAt(0)
			: 'X';
	}
	
	public int newArrayType() {
		switch (desc) {
			case 'Z':
				return T_BOOLEAN;
			case 'C':
				return T_CHAR;
			case 'S':
				return T_SHORT;
			case 'I':
				return T_INT;
			case 'J':
				return T_LONG;
			case 'F':
				return T_FLOAT;
			case 'D':
				return T_DOUBLE;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tRETURN() {
		switch (desc) {
			case 'Z':
			case 'C':
			case 'S':
			case 'I':
				return IRETURN;
			case 'J':
				return LRETURN;
			case 'F':
				return FRETURN;
			case 'D':
				return DRETURN;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tALOAD() {
		switch (desc) {
			case 'Z':
				return BALOAD;
			case 'C':
				return CALOAD;
			case 'S':
				return SALOAD;
			case 'I':
				return IALOAD;
			case 'J':
				return LALOAD;
			case 'F':
				return FALOAD;
			case 'D':
				return DALOAD;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tLOAD() {
		switch (desc) {
			case 'Z': // huh?
			case 'C':
			case 'S':
			case 'I':
				return ILOAD;
			case 'J':
				return LLOAD;
			case 'F':
				return FLOAD;
			case 'D':
				return DLOAD;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tSTORE() {
		switch (desc) {
			case 'Z':
			case 'C':
			case 'S':
			case 'I':
				return ISTORE;
			case 'J':
				return LSTORE;
			case 'F':
				return FSTORE;
			case 'D':
				return DSTORE;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tASTORE() {
		switch (desc) {
			case 'Z':
				return BASTORE;
			case 'C':
				return CASTORE;
			case 'S':
				return SASTORE;
			case 'I':
				return IASTORE;
			case 'J':
				return LASTORE;
			case 'F':
				return FASTORE;
			case 'D':
				return DASTORE;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public static int tCONST(FieldDescriptor fd) {
		assert(fd.desc.length() == 1);
		
		char desc = fd.desc.charAt(0);
		switch (desc) {
			case 'Z':
			case 'C':
			case 'S':
			case 'I':
				return ICONST_0;
			case 'J':
				return LCONST_0;
			case 'F':
				return FCONST_0;
			case 'D':
				return DCONST_0;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tCONST() {
		switch (desc) {
			case 'Z':
			case 'C':
			case 'S':
			case 'I':
				return ICONST_0;
			case 'J':
				return LCONST_0;
			case 'F':
				return FCONST_0;
			case 'D':
				return DCONST_0;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	/* arithmetic */
	
	public int tADD() {
		switch (desc) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IADD;
			case 'J':
				return LADD;
			case 'F':
				return FADD;
			case 'D':
				return DADD;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	
	public int tDIV() {
		switch (desc) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IDIV;
			case 'J':
				return LDIV;
			case 'F':
				return FDIV;
			case 'D':
				return DDIV;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tMUL() {
		switch (desc) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IMUL;
			case 'J':
				return LMUL;
			case 'F':
				return FMUL;
			case 'D':
				return DMUL;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tNEG() {
		switch (desc) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return INEG;
			case 'J':
				return LNEG;
			case 'F':
				return FNEG;
			case 'D':
				return DNEG;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tREM() {
		switch (desc) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IREM;
			case 'J':
				return LREM;
			case 'F':
				return FREM;
			case 'D':
				return DREM;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
	
	public int tSUB() {
		switch (desc) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return ISUB;
			case 'J':
				return LSUB;
			case 'F':
				return FSUB;
			case 'D':
				return DSUB;
			default:
				String err = String.format("Unknown type: '%s'", desc);
				throw new RuntimeException(err);
		}
	}
}
