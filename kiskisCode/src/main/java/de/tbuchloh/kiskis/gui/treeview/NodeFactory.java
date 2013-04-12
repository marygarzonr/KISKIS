/*
 * Copyright (C) 2004 by Tobias Buchloh.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.sourceforge.net/projects/kiskis
 */

package de.tbuchloh.kiskis.gui.treeview;

import java.util.Hashtable;

import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.SecuredFile;

/**
 * <b>NodeFactory</b>:
 * 
 * @author gandalf
 * @version $Id: NodeFactory.java,v 1.6 2007/02/18 14:37:52 tbuchloh Exp $
 */
@SuppressWarnings("rawtypes")
//public abstract class NodeFactory {
  public class NodeFactory {    

    private static NodeFactory instance = null;
    
    private NodeFactory(){
        
    }
    
    public static NodeFactory getInstance(){
        
        if(instance == null){
            instance = new NodeFactory();
        }
        
        return instance;
    }
    
    private static final Hashtable<Class, Class> CLASS_MAP = initNodeMap();

    public MyTreeNode create(final ModelNode el) {
	try {
	    
	    MyTreeNode n = null;
	    
	    if(el instanceof Group){
	        n = new GroupNode();  
	    }else if(el instanceof SecuredElement){
	        n = new SecuredFileNode();	        
	    }else if(el instanceof BankAccount){
	        n = new BankAccountNode();
	    }else if (el instanceof CreditCard){
	        n = new CreditCardNode();
	    }else if(el instanceof GenericAccount){
	        n = new GenericAccountNode();
	    }else if(el instanceof NetAccount){
	        n = new NetAccountNode();
	    }
	    
	        
	    if(n != null){
	        n.setModelNode(el);
	    }
	    
	    return n;
	    
	    
//	    final Class c = CLASS_MAP.get(el.getClass());
//	    final MyTreeNode n = (MyTreeNode) c.newInstance();
//	    n.setModelNode(el);

	    // // create subtree
	    // // TODO 01.11.2010, gandalf: Could be optimized
	    // if (n instanceof GroupNode) {
	    // final GroupNode g = (GroupNode) n;
	    // for (final ModelNode i : ((Group) el).getChildren()) {
	    // g.add(create(i));
	    // }
	    // }
//	    return n;
	} catch (final Exception e) {
	    throw new Error("should never happen!", e); //$NON-NLS-1$
	}
    }

    /**
     * initializes the map which stores (Class, Class)-pairs. The key is the
     * model class, the value is the node class.
     * 
     * @return the map
     */
    private static Hashtable<Class, Class> initNodeMap() {
	final Hashtable<Class, Class> map = new Hashtable<Class, Class>();
	map.put(Group.class, GroupNode.class);
	map.put(BankAccount.class, BankAccountNode.class);
	map.put(CreditCard.class, CreditCardNode.class);
	map.put(GenericAccount.class, GenericAccountNode.class);
	map.put(NetAccount.class, NetAccountNode.class);
	map.put(SecuredFile.class, SecuredFileNode.class);
	return map;
    }

}
