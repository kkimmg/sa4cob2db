package k_kim_mg.sa4cob2db.test;
import k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter;
import k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent;
public class CodeGeneratorAdapterTest extends CodeGeneratorAdapter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postClose(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postClose(CodeGeneratorEvent e) {
		System.err.println("postClose" + e.getFile().toString() + e.getPeriod());
	}
	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postCommit(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
     */
    @Override
    public void postCommit(CodeGeneratorEvent e) {
    	System.err.println("postCommit");
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postDelete(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postDelete(CodeGeneratorEvent e) {
		System.err.println("postDelete" + e.getFile().toString() + e.getPeriod());
	}
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postGetOption(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postGetOption(CodeGeneratorEvent e) {
		System.err.println("postGetOption" + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postInitialize(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postInitialize(CodeGeneratorEvent e) {
		System.err.println("postInitialize");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postMoveRead(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postMoveRead(CodeGeneratorEvent e) {
		System.err.println("postMoveRead" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postOpen(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postOpen(CodeGeneratorEvent e) {
		System.err.println("postOpen" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postReadNext(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postReadNext(CodeGeneratorEvent e) {
		System.err.println("postReadNext" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postRewrite(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postRewrite(CodeGeneratorEvent e) {
		System.err.println("postRewrite" + e.getFile().toString() + e.getPeriod());
	}
	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postRollback(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
     */
    @Override
    public void postRollback(CodeGeneratorEvent e) {
    	System.err.println("postRollbacke");
    }
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postSetOption(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postSetOption(CodeGeneratorEvent e) {
		System.err.println("postSetOption" + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postStart(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postStart(CodeGeneratorEvent e) {
		System.err.println("postStart" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postTerminate(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postTerminate(CodeGeneratorEvent e) {
		System.err.println("postTerminate");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#postWrite(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void postWrite(CodeGeneratorEvent e) {
		System.err.println("postWrite" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preClose(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preClose(CodeGeneratorEvent e) {
		System.err.println("preClose" + e.getFile().toString() + e.getPeriod());
	}
	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preCommit(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
     */
    @Override
    public void preCommit(CodeGeneratorEvent e) {
    	System.err.println("preCommit");
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preDelete(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preDelete(CodeGeneratorEvent e) {
		System.err.println("preDelete" + e.getFile().toString() + e.getPeriod());
	}
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preGetOption(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preGetOption(CodeGeneratorEvent e) {
		System.err.println("preGetOption" + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preInitialize(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preInitialize(CodeGeneratorEvent e) {
		System.err.println("preInitialize");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preMoveRead(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preMoveRead(CodeGeneratorEvent e) {
		System.err.println("preMoveRead" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preOpen(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preOpen(CodeGeneratorEvent e) {
		System.err.println("preOpen" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preReadNext(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preReadNext(CodeGeneratorEvent e) {
		System.err.println("preReadNext" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preRewrite(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preRewrite(CodeGeneratorEvent e) {
		System.err.println("preRewrite" + e.getFile().toString() + e.getPeriod());
	}
	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preRollback(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
     */
    @Override
    public void preRollback(CodeGeneratorEvent e) {
    	System.err.println("preRollback");
    }
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preSetOption(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preSetOption(CodeGeneratorEvent e) {
		System.err.println("preSetOption" + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preStart(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preStart(CodeGeneratorEvent e) {
		System.err.println("preStart" + e.getFile().toString() + e.getPeriod());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preTerminate(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preTerminate(CodeGeneratorEvent e) {
		System.err.println("preTerminate");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGeneratorAdapter#preWrite(k_kim_mg.sa4cob2db.codegen.CodeGeneratorEvent)
	 */
	@Override
	public void preWrite(CodeGeneratorEvent e) {
		System.err.println("preWrite" + e.getFile().toString() + e.getPeriod());
	}
}
