/**
 *
 */
package de.tbuchloh.kiskis.model;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: ModelNodeVisitorAdapter.java,v 1.1 2007/03/11 22:14:14 tbuchloh
 *          Exp $
 */
public abstract class ModelNodeVisitorAdapter implements ModelNodeVisitor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model
	 * .Attachment)
	 */
	public void visit(final Attachment attachment) {
		// does nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model
	 * .BankAccount)
	 */
	public void visit(final BankAccount account) {
		// does nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model
	 * .CreditCard)
	 */
	public void visit(final CreditCard card) {
		// does nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model
	 * .GenericAccount)
	 */
	public void visit(final GenericAccount account) {
		// does nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model
	 * .Group)
	 */
	public void visit(final Group group) {
		// does nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model
	 * .NetAccount)
	 */
	public void visit(final NetAccount account) {
		// does nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model
	 * .SecuredFile)
	 */
	public void visit(final SecuredFile account) {
		// does nothing
	}

}
