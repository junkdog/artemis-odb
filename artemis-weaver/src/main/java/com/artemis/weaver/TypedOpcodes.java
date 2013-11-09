package com.artemis.weaver;

import static com.artemis.meta.ClassMetadataUtil.instanceFieldTypes;

import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;

public final class TypedOpcodes implements Opcodes {

	private final ClassMetadata meta;
	private final char type;

	public TypedOpcodes(ClassMetadata meta) {
		this.meta = meta;
		String type = instanceFieldTypes(meta).iterator().next();
		assert(type.length() <= 1);
		
		this.type = (type.length() == 1)
			? type.charAt(0)
			: 'X';
	}
	
	public int newArrayType() {
		switch (type) {
			case 'B':
				return T_BOOLEAN;
			case 'C':
				return T_CHAR;
			case 'S':
				return T_SHORT;
			case 'I':
				return T_INT;
			case 'L':
				return T_LONG;
			case 'F':
				return T_FLOAT;
			case 'D':
				return T_DOUBLE;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tRETURN() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IRETURN;
			case 'L':
				return LRETURN;
			case 'F':
				return FRETURN;
			case 'D':
				return DRETURN;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tALOAD() {
		switch (type) {
			case 'B':
				return BALOAD;
			case 'C':
				return CALOAD;
			case 'S':
				return SALOAD;
			case 'I':
				return IALOAD;
			case 'L':
				return LALOAD;
			case 'F':
				return FALOAD;
			case 'D':
				return DALOAD;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tSTORE() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return ISTORE;
			case 'L':
				return LSTORE;
			case 'F':
				return FSTORE;
			case 'D':
				return DSTORE;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tASTORE() {
		switch (type) {
			case 'B':
				return BASTORE;
			case 'C':
				return CASTORE;
			case 'S':
				return SASTORE;
			case 'I':
				return IASTORE;
			case 'L':
				return LASTORE;
			case 'F':
				return FASTORE;
			case 'D':
				return DASTORE;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	/* arithmetic */
	
	public int tADD() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IADD;
			case 'L':
				return LADD;
			case 'F':
				return FADD;
			case 'D':
				return DADD;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	
	public int tDIV() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IDIV;
			case 'L':
				return LDIV;
			case 'F':
				return FDIV;
			case 'D':
				return DDIV;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tMUL() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IMUL;
			case 'L':
				return LMUL;
			case 'F':
				return FMUL;
			case 'D':
				return DMUL;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tNEG() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return INEG;
			case 'L':
				return LNEG;
			case 'F':
				return FNEG;
			case 'D':
				return DNEG;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tREM() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return IREM;
			case 'L':
				return LREM;
			case 'F':
				return FREM;
			case 'D':
				return DREM;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
	
	public int tSUB() {
		switch (type) {
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return ISUB;
			case 'L':
				return LSUB;
			case 'F':
				return FSUB;
			case 'D':
				return DSUB;
			default:
				String err = String.format("Unknown type: '%s'", type);
				throw new RuntimeException(err);
		}
	}
}
